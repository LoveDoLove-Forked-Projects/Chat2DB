package ai.chat2db.community.domain.core.impl.task.export.sql;

import ai.chat2db.community.domain.api.enums.ExportFileSuffixEnum;
import ai.chat2db.community.domain.api.model.metadata.DataType;
import ai.chat2db.community.domain.api.model.task.extension.ExportCell;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan;
import ai.chat2db.community.domain.api.model.value.SQLDataValue;
import ai.chat2db.community.domain.core.impl.db.extension.SqlExecutionPolicyManager;
import ai.chat2db.community.domain.core.impl.task.export.BaseExporter;
import ai.chat2db.community.domain.core.impl.task.export.ExportCellProcessorChain;
import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.ISqlBuilder;
import ai.chat2db.spi.IValueProcessor;
import ai.chat2db.spi.model.request.MultiInsertSqlRequest;
import ai.chat2db.spi.model.request.SingleInsertSqlRequest;
import ai.chat2db.spi.model.request.UpdateSqlRequest;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.ResultSetUtils;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


@Slf4j
@Component
public class SqlDataExporter extends BaseExporter {

    public SqlDataExporter(ExportCellProcessorChain exportCellProcessorChain,
            SqlExecutionPolicyManager sqlExecutionPolicyManager) {
        super(exportCellProcessorChain, sqlExecutionPolicyManager);
        this.suffix = ExportFileSuffixEnum.SQL.getSuffix();
        this.contentType = "text/sql";
    }

    @Override
    public String type() {
        return "sql";
    }


    @Override
    protected void singleExport(ExportAsyncContext asyncContext, String tableName, File file) {
        Connection connection = Chat2DBContext.getConnection();
        SqlExecutionPlan executionPlan = getQueryPlan(tableName);
        asyncContext.info(String.format("Exporting data from table %s to %s", tableName, file.getAbsolutePath()));
        try (PrintWriter writer = new PrintWriter(file);) {
            exportSql(connection, executionPlan, asyncContext, tableName, writer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportSql(Connection connection, SqlExecutionPlan executionPlan, ExportAsyncContext asyncContext,
            String tableName, PrintWriter writer) {
        String databaseName = Chat2DBContext.getConnectInfo().getDatabaseName();
        String schemaName = Chat2DBContext.getConnectInfo().getSchemaName();
        Boolean containsHeader = asyncContext.getContainsHeader();
        IDbMetaData metaData = Chat2DBContext.getDbMetaData();
        ISqlBuilder sqlBuilder = metaData.getSqlBuilder();
        IValueProcessor valueProcessor = metaData.getValueProcessor();
        String sqyType = asyncContext.getSqyType();

        switch (sqyType) {
            case "single" -> exportSingleInsert(connection, executionPlan, containsHeader, sqlBuilder,
                    valueProcessor, databaseName, schemaName, tableName, writer, asyncContext);
            case "multi" -> exportMultiInsert(connection, executionPlan, containsHeader, sqlBuilder,
                    valueProcessor, databaseName, schemaName, tableName, writer, asyncContext);
            case "update" -> exportUpdate(connection, executionPlan, sqlBuilder, valueProcessor,
                    databaseName, schemaName, tableName, writer, asyncContext);
            default -> throw new IllegalArgumentException("Unsupported sqyType: " + sqyType);
        }
    }

    private void exportSingleInsert(Connection connection, SqlExecutionPlan executionPlan, Boolean containsHeader,
                                    ISqlBuilder sqlBuilder, IValueProcessor valueProcessor,
                                    String databaseName, String schemaName, String tableName, PrintWriter writer,ExportAsyncContext asyncContext) {
        List<String> sqlList = new ArrayList<>(BATCH_SIZE);
        DefaultSQLExecutor.getInstance().execute(connection, executionPlan.getSql(), BATCH_SIZE, resultSet -> {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            List<Integer> includedColumnIndexes = includedColumnIndexes(resultSetMetaData, executionPlan);
            requireAuthorizedColumns(includedColumnIndexes);
            List<String> header = exportHeader(resultSet, includedColumnIndexes, containsHeader,
                    resultSetMetaData.getColumnCount());
            int n = 0;
            boolean hasNext = nextRow(resultSet, executionPlan, n);
            while (hasNext) {
                asyncContext.checkCancelled();
                List<String> rowData = extractRowData(resultSet, valueProcessor, tableName,
                        includedColumnIndexes);
                String sql = sqlBuilder.dml().buildInsert(SingleInsertSqlRequest.builder()
                        .tableName(tableName)
                        .columnList(header)
                        .valueList(rowData)
                        .build());
                sqlList.add(sql + ";");
                n++;
                hasNext = nextRow(resultSet, executionPlan, n);
                if (sqlList.size() >= BATCH_SIZE || !hasNext) {
                    writeSqlList(writer, sqlList);
                    asyncContext.info(DateUtil.formatTime(new Date()) + ":" + String.format("Exported %d rows", n));
                }
            }
            writeSqlList(writer, sqlList);
        }, asyncContext, asyncContext::checkCancelled);
    }

    private void exportMultiInsert(Connection connection, SqlExecutionPlan executionPlan, Boolean containsHeader,
                                   ISqlBuilder sqlBuilder, IValueProcessor valueProcessor,
                                   String databaseName, String schemaName, String tableName, PrintWriter writer,ExportAsyncContext asyncContext) {
        DefaultSQLExecutor.getInstance().execute(connection, executionPlan.getSql(), BATCH_SIZE, resultSet -> {
            List<List<String>> dataList = new ArrayList<>(BATCH_SIZE);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            List<Integer> includedColumnIndexes = includedColumnIndexes(resultSetMetaData, executionPlan);
            requireAuthorizedColumns(includedColumnIndexes);
            List<String> header = exportHeader(resultSet, includedColumnIndexes, containsHeader,
                    resultSetMetaData.getColumnCount());
            int rowCount = 0;
            while (nextRow(resultSet, executionPlan, rowCount)) {
                asyncContext.checkCancelled();
                dataList.add(extractRowData(resultSet, valueProcessor, tableName, includedColumnIndexes));
                rowCount++;
            }
            String sql = sqlBuilder.dml().buildBatchInsert(MultiInsertSqlRequest.builder()
                    .tableName(tableName)
                    .columnList(header)
                    .valueLists(dataList)
                    .build());
            writer.println(sql+";");
            writer.flush();
        }, asyncContext, asyncContext::checkCancelled);
    }

    private void exportUpdate(Connection connection, SqlExecutionPlan executionPlan, ISqlBuilder sqlBuilder,
                              IValueProcessor valueProcessor,
                              String databaseName, String schemaName, String tableName, PrintWriter writer,ExportAsyncContext asyncContext) {
        List<String> sqlList = new ArrayList<>(BATCH_SIZE);
        DefaultSQLExecutor.getInstance().execute(connection, executionPlan.getSql(), BATCH_SIZE, resultSet -> {
            Map<String, String> primaryKeyMap = getPrimaryKeyMap(connection, databaseName, schemaName, tableName);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            List<Integer> includedColumnIndexes = includedColumnIndexes(resultSetMetaData, executionPlan);
            requireAuthorizedUpdateColumns(resultSetMetaData, includedColumnIndexes, primaryKeyMap);
            int n = 0;
            while (nextRow(resultSet, executionPlan, n)) {
                asyncContext.checkCancelled();
                Map<String, String> row = extractRowDataAsMap(resultSet, valueProcessor, primaryKeyMap, tableName,
                        includedColumnIndexes);
                String sql = sqlBuilder.dml().buildUpdate(UpdateSqlRequest.builder()
                        .databaseName(databaseName)
                        .schemaName(schemaName)
                        .tableName(tableName)
                        .row(row)
                        .primaryKeyMap(primaryKeyMap)
                        .build());
                sqlList.add(sql);
                n++;
                if (sqlList.size() >= BATCH_SIZE || resultSet.isLast()) {
                    writeSqlList(writer, sqlList);
                    asyncContext.info(DateUtil.formatTime(new Date()) + ":" + String.format("Exported %d rows", n));

                }
            }
            writeSqlList(writer, sqlList);
        }, asyncContext, asyncContext::checkCancelled);
    }

    private List<String> extractRowData(ResultSet resultSet, IValueProcessor valueProcessor, String tableName,
            List<Integer> includedColumnIndexes) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> rowData = new ArrayList<>(includedColumnIndexes.size());
        for (Integer columnIndex : includedColumnIndexes) {
            rowData.add(processSqlCell(resultSet, metaData, columnIndex, valueProcessor, tableName));
        }
        return rowData;
    }

    private Map<String, String> extractRowDataAsMap(ResultSet resultSet, IValueProcessor valueProcessor,
            Map<String, String> primaryKeyMap, String tableName, List<Integer> includedColumnIndexes)
            throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<String, String> row = new HashMap<>(includedColumnIndexes.size());
        for (Integer columnIndex : includedColumnIndexes) {
            String columnName = metaData.getColumnName(columnIndex);
            String jdbcValueString = processSqlCell(resultSet, metaData, columnIndex, valueProcessor, tableName);
            if (primaryKeyMap.containsKey(columnName)) {
                primaryKeyMap.put(columnName, jdbcValueString);
            } else {
                row.put(columnName, jdbcValueString);
            }
        }
        return row;
    }

    private List<String> exportHeader(ResultSet resultSet, List<Integer> includedColumnIndexes,
            Boolean containsHeader, int columnCount) throws SQLException {
        if (!Boolean.TRUE.equals(containsHeader) && includedColumnIndexes.size() == columnCount) {
            return null;
        }
        return selectColumns(ResultSetUtils.getRsHeader(resultSet), includedColumnIndexes);
    }

    private void requireAuthorizedColumns(List<Integer> includedColumnIndexes) {
        if (includedColumnIndexes.isEmpty()) {
            throw new IllegalStateException("SQL export has no authorized columns");
        }
    }

    private void requireAuthorizedUpdateColumns(ResultSetMetaData metaData, List<Integer> includedColumnIndexes,
            Map<String, String> primaryKeyMap) throws SQLException {
        requireAuthorizedColumns(includedColumnIndexes);
        Set<String> includedColumnNames = new HashSet<>(includedColumnIndexes.size());
        for (Integer columnIndex : includedColumnIndexes) {
            includedColumnNames.add(metaData.getColumnName(columnIndex));
        }
        for (String primaryKey : primaryKeyMap.keySet()) {
            if (!includedColumnNames.contains(primaryKey)) {
                throw new IllegalStateException("SQL update export cannot expose a restricted primary key column");
            }
        }
        boolean hasWritableColumn = includedColumnNames.stream().anyMatch(column -> !primaryKeyMap.containsKey(column));
        if (!hasWritableColumn) {
            throw new IllegalStateException("SQL update export has no authorized writable columns");
        }
    }

    private String processSqlCell(ResultSet resultSet, ResultSetMetaData metaData, int columnIndex,
            IValueProcessor valueProcessor, String tableName) throws SQLException {
        JDBCDataValue jdbcDataValue = new JDBCDataValue(resultSet, metaData, columnIndex, false);
        if (!hasExportCellProcessors()) {
            return valueProcessor.getJdbcSqlValueString(jdbcDataValue);
        }
        ExportCell processedCell = processJdbcCell(metaData, columnIndex, tableName, jdbcDataValue);

        DataType dataType = new DataType();
        dataType.setDataTypeName(processedCell.getTypeName());
        dataType.setPrecision(processedCell.getPrecision());
        dataType.setScale(processedCell.getScale());
        SQLDataValue sqlDataValue = new SQLDataValue();
        sqlDataValue.setDataType(dataType);
        Object processedValue = processedCell.getValue();
        sqlDataValue.setValue(toSqlValue(processedValue));
        return valueProcessor.getSqlValueString(sqlDataValue);
    }

    private String toSqlValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof byte[] bytes) {
            return "0x" + HexFormat.of().withUpperCase().formatHex(bytes);
        }
        if (value instanceof char[] chars) {
            return new String(chars);
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<String> values = new ArrayList<>(length);
            for (int index = 0; index < length; index++) {
                values.add(toSqlValue(Array.get(value, index)));
            }
            return values.toString();
        }
        if (value instanceof Collection<?> values) {
            return values.stream().map(this::toSqlValue).toList().toString();
        }
        if (value instanceof Map<?, ?> values) {
            Map<String, String> serialized = new LinkedHashMap<>(values.size());
            values.forEach((key, mapValue) -> serialized.put(toSqlValue(key), toSqlValue(mapValue)));
            return serialized.toString();
        }
        return String.valueOf(value);
    }

    private Map<String, String> getPrimaryKeyMap(Connection connection, String databaseName,
                                                 String schemaName, String tableName) throws SQLException {
        Map<String, String> primaryKeyMap = new HashMap<>();
        try (ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(databaseName, schemaName, tableName)) {
            while (primaryKeys.next()) {
                primaryKeyMap.put(primaryKeys.getString("COLUMN_NAME"), "");
            }
        }
        return primaryKeyMap;
    }

    private void writeSqlList(PrintWriter writer, List<String> sqlList) {
        if(CollectionUtils.isEmpty(sqlList)){
            return;
        }
        sqlList.forEach(writer::println);
        sqlList.clear();
    }

}
