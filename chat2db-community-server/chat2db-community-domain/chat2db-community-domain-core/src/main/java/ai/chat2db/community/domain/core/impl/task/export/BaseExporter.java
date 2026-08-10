package ai.chat2db.community.domain.core.impl.task.export;

import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.model.task.extension.ExportCell;
import ai.chat2db.community.domain.api.model.task.extension.ExportCellContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionContext;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionOperation;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan;
import ai.chat2db.community.domain.api.model.sql.extension.SqlResultColumnContext;
import ai.chat2db.community.domain.core.impl.db.extension.SqlExecutionPolicyManager;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;


@Slf4j
public abstract class BaseExporter implements IExportStrategy {

    private final ExportCellProcessorChain exportCellProcessorChain;
    private final SqlExecutionPolicyManager sqlExecutionPolicyManager;

    protected String contentType;

    protected String suffix;
    public static int BATCH_SIZE = 1000;

    protected BaseExporter(ExportCellProcessorChain exportCellProcessorChain,
            SqlExecutionPolicyManager sqlExecutionPolicyManager) {
        this.exportCellProcessorChain = exportCellProcessorChain;
        this.sqlExecutionPolicyManager = sqlExecutionPolicyManager;
    }

    @Override
    public void run(ExportAsyncContext asyncContext) {
        asyncContext.checkCancelled();
        List<String> tableNames = asyncContext.getTableNames();
        if (CollectionUtils.isEmpty(tableNames)) {
            throw new IllegalArgumentException("tableNames should not be null or empty");
        }
        try {
            File parent = asyncContext.getWriteFile().getParentFile();
            if (parent != null) {
                FileUtil.mkdir(parent);
            }
            if (tableNames.size() == 1) {
                asyncContext.setProgress(20);
                single(asyncContext);
            } else {
                multi(asyncContext);
            }
        } catch (CancellationException e) {
            deleteOutputFile(asyncContext);
            throw e;
        } catch (Exception e) {
            asyncContext.error("export data error, " + e.getMessage());
            log.error("export data error", e);
            deleteOutputFile(asyncContext);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Export data failed", e);
        }
    }

    private void single(ExportAsyncContext asyncContext) throws IOException, SQLException {
        asyncContext.info(String.format("Exporting table %s", asyncContext.getTableNames().get(0)));
        singleExport(asyncContext, asyncContext.getTableNames().get(0), asyncContext.getWriteFile());
    }

    private void multi(ExportAsyncContext asyncContext) throws IOException, SQLException {
        String path = asyncContext.getWriteFile().getParent();
        FileUtil.mkdir(path);
        int n = asyncContext.getTableNames().size();
        String[] paths = new String[n];
        InputStream[] inputStreams = new InputStream[n];
        File[] temporaryFiles = new File[n];
        try {
            for (int i = 0; i < n; i++) {
                asyncContext.checkCancelled();
                String tableName = asyncContext.getTableNames().get(i);
                if (StringUtils.isEmpty(tableName)) {
                    throw new IllegalArgumentException("tableName should not be null or empty");
                }
                File file = new File(path + File.separator + tableName + suffix);
                temporaryFiles[i] = file;
                asyncContext.info(String.format("Exporting table %s", tableName));
                singleExport(asyncContext, tableName, file);
                paths[i] = tableName + suffix;
                inputStreams[i] = FileUtil.getInputStream(file);
            }
            asyncContext.checkCancelled();
            ZipUtil.zip(asyncContext.getWriteFile(), paths, inputStreams);
        } finally {
            closeStreams(inputStreams);
            deleteFiles(temporaryFiles);
        }
    }

    private void closeStreams(InputStream[] inputStreams) {
        for (InputStream inputStream : inputStreams) {
            if (inputStream == null) {
                continue;
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close export temporary file", e);
            }
        }
    }

    private void deleteFiles(File[] files) {
        for (File file : files) {
            if (file != null && file.exists() && !FileUtil.del(file)) {
                log.warn("Failed to delete export temporary file: {}", file.getAbsolutePath());
            }
        }
    }

    private void deleteOutputFile(ExportAsyncContext asyncContext) {
        File writeFile = asyncContext.getWriteFile();
        if (writeFile != null && writeFile.exists() && !FileUtil.del(writeFile)) {
            log.error("Failed to delete incomplete export file: {}", writeFile.getAbsolutePath());
        }
    }
    protected String getQuerySql(String tableName) {
        String databaseName = Chat2DBContext.getConnectInfo().getDatabaseName();
        String schemaName = Chat2DBContext.getConnectInfo().getSchemaName();
        return Chat2DBContext.getSqlBuilder().dql().buildSelectTable(databaseName, schemaName, tableName);
    }

    protected SqlExecutionPlan getQueryPlan(String tableName) {
        String querySql = getQuerySql(tableName);
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        SqlExecutionContext context = new SqlExecutionContext(
                connectInfo == null ? null : connectInfo.getDataSourceId(),
                connectInfo == null ? null : connectInfo.getDbType(),
                connectInfo == null ? null : connectInfo.getDatabaseName(),
                connectInfo == null ? null : connectInfo.getSchemaName(),
                tableName, querySql, SqlExecutionOperation.EXPORT, type());
        SqlExecutionPlan plan = sqlExecutionPolicyManager.plan(context);
        sqlExecutionPolicyManager.beforeExecute(plan);
        return plan;
    }

    protected boolean isRowAllowed(SqlExecutionPlan plan, int exportedRowCount) {
        return sqlExecutionPolicyManager.isRowAllowed(plan, exportedRowCount);
    }

    protected boolean nextRow(ResultSet resultSet, SqlExecutionPlan plan, int exportedRowCount)
            throws SQLException {
        return isRowAllowed(plan, exportedRowCount) && resultSet.next();
    }

    protected ExportCell processCell(ResultSetMetaData metaData, int columnIndex, String tableName,
            Object value) throws SQLException {
        ExportCell cell = createCell(metaData, columnIndex, value);
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        ExportCellContext cellContext = new ExportCellContext(
                connectInfo == null ? null : connectInfo.getDataSourceId(),
                connectInfo == null ? null : connectInfo.getDbType(),
                connectInfo == null ? null : connectInfo.getDatabaseName(),
                connectInfo == null ? null : connectInfo.getSchemaName(),
                tableName, metaData.getColumnName(columnIndex), type());
        return exportCellProcessorChain.process(cellContext, cell);
    }

    protected ExportCell processJdbcCell(ResultSetMetaData metaData, int columnIndex, String tableName,
            JDBCDataValue jdbcDataValue) throws SQLException {
        Object value = jdbcDataValue.getObject();
        if (value == null) {
            value = jdbcDataValue.getStringValue();
        }
        return processCell(metaData, columnIndex, tableName, value);
    }

    protected ExportCell createCell(ResultSetMetaData metaData, int columnIndex, Object value) throws SQLException {
        return new ExportCell(value, metaData.getColumnType(columnIndex),
                metaData.getColumnTypeName(columnIndex), metaData.getPrecision(columnIndex),
                metaData.getScale(columnIndex));
    }

    protected List<Integer> includedColumnIndexes(ResultSetMetaData metaData, SqlExecutionPlan plan)
            throws SQLException {
        int columnCount = metaData.getColumnCount();
        List<Integer> includedIndexes = new ArrayList<>(columnCount);
        if (sqlExecutionPolicyManager.isEmpty()) {
            for (int index = 1; index <= columnCount; index++) {
                includedIndexes.add(index);
            }
            return includedIndexes;
        }
        SqlExecutionContext executionContext = plan.getContext();
        for (int index = 1; index <= columnCount; index++) {
            String resultTableName = StringUtils.defaultIfBlank(metaData.getTableName(index),
                    executionContext.getTableName());
            SqlResultColumnContext columnContext = new SqlResultColumnContext(plan, index,
                    metaData.getColumnName(index), metaData.getColumnLabel(index), metaData.getColumnType(index),
                    metaData.getColumnTypeName(index), executionContext.getDatabaseName(),
                    executionContext.getSchemaName(), resultTableName, false);
            if (sqlExecutionPolicyManager.includeColumn(columnContext)) {
                includedIndexes.add(index);
            }
        }
        return includedIndexes;
    }

    protected <T> List<T> selectColumns(List<T> values, List<Integer> includedColumnIndexes) {
        List<T> selected = new ArrayList<>(includedColumnIndexes.size());
        for (Integer columnIndex : includedColumnIndexes) {
            int listIndex = columnIndex - 1;
            if (listIndex >= 0 && listIndex < values.size()) {
                selected.add(values.get(listIndex));
            }
        }
        return selected;
    }

    protected boolean hasExportCellProcessors() {
        return !exportCellProcessorChain.isEmpty();
    }

    protected abstract void singleExport(ExportAsyncContext asyncContext, String tableName, File file) throws IOException, SQLException;

}
