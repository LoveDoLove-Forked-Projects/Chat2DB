package ai.chat2db.community.domain.core.impl.task.export.excel;

import ai.chat2db.community.domain.core.impl.task.export.BaseExporter;
import ai.chat2db.community.domain.core.impl.task.export.ExportCellProcessorChain;
import ai.chat2db.community.domain.core.impl.db.extension.SqlExecutionPolicyManager;
import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.model.task.extension.ExportCell;
import ai.chat2db.community.domain.api.model.sql.extension.SqlExecutionPlan;
import ai.chat2db.spi.IValueProcessor;
import ai.chat2db.spi.model.value.JDBCDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.ResultSetUtils;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.SpreadsheetVersion;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;


@Slf4j
public abstract class BaseExcelExporter extends BaseExporter {

    protected BaseExcelExporter(ExportCellProcessorChain exportCellProcessorChain,
            SqlExecutionPolicyManager sqlExecutionPolicyManager) {
        super(exportCellProcessorChain, sqlExecutionPolicyManager);
    }

    static {
        SpreadsheetVersion excel2007 = SpreadsheetVersion.EXCEL2007;
        SpreadsheetVersion excel97 = SpreadsheetVersion.EXCEL97;
        if (Integer.MAX_VALUE != excel2007.getMaxTextLength()) {
            Field field;
            try {
                field = excel2007.getClass().getDeclaredField("_maxTextLength");
                field.setAccessible(true);
                field.set(excel2007, Integer.MAX_VALUE);
            } catch (Exception e) {
                log.error("Error setting max text length", e);
            }
        }
        if (Integer.MAX_VALUE != excel97.getMaxTextLength()) {
            Field field;
            try {
                field = excel97.getClass().getDeclaredField("_maxTextLength");
                field.setAccessible(true);
                field.set(excel97, Integer.MAX_VALUE);
            } catch (Exception e) {
                log.error("Error setting max text length", e);
            }
        }
    }

    @Override
    protected void singleExport(ExportAsyncContext asyncContext, String tableName, File file) {
        ExcelTypeEnum excelType = getExcelType();
        SqlExecutionPlan executionPlan = getQueryPlan(tableName);
        Connection connection = Chat2DBContext.getConnection();
        asyncContext.info(String.format("Exporting data from table %s to %s", tableName, file.getAbsolutePath()));
        DefaultSQLExecutor.getInstance().execute(connection, executionPlan.getSql(), BATCH_SIZE, resultSet ->
                writeExcelData(resultSet, excelType, file, tableName, executionPlan, asyncContext),
                asyncContext, asyncContext::checkCancelled);
    }

    public static int BATCH_SIZE = 500;

    private void writeExcelData(ResultSet resultSet, ExcelTypeEnum excelType, File file, String sheetName,
            SqlExecutionPlan executionPlan, ExportAsyncContext asyncContext) {
        try (ExcelWriter excelWriter = EasyExcel.write(file).excelType(excelType).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<Integer> includedColumnIndexes = includedColumnIndexes(metaData, executionPlan);
            IValueProcessor valueProcessor = Chat2DBContext.getDbMetaData().getValueProcessor();
            if (Boolean.TRUE.equals(asyncContext.getContainsHeader())) {
                List<String> header = selectColumns(ResultSetUtils.getRsHeader(resultSet), includedColumnIndexes);
                writeSheet.setHead(header.stream().map(Collections::singletonList).collect(Collectors.toList()));
            }
            int n = 0;
            boolean hasNext = nextRow(resultSet, executionPlan, n);
            while (hasNext) {
                asyncContext.checkCancelled();
                List<Object> rowDataList = new ArrayList<>(includedColumnIndexes.size());
                for (Integer columnIndex : includedColumnIndexes) {
                    JDBCDataValue jdbcDataValue = new JDBCDataValue(resultSet, metaData, columnIndex, false);
                    if (hasExportCellProcessors()) {
                        ExportCell cell = processJdbcCell(metaData, columnIndex, sheetName, jdbcDataValue);
                        rowDataList.add(cell.getValue());
                    } else {
                        rowDataList.add(valueProcessor.getJdbcValue(jdbcDataValue));
                    }
                }
                excelWriter.write(Collections.singletonList(rowDataList), writeSheet);
                n++;
                hasNext = nextRow(resultSet, executionPlan, n);
                if (n % BATCH_SIZE == 0 || !hasNext) {
                    asyncContext.info(DateUtil.formatTime(new Date()) + ":" + String.format("Exported %d rows", n));
                }
            }
        } catch (CancellationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error writing Excel data", e);
            throw new IllegalStateException("Error writing Excel data", e);
        }
    }


    protected abstract ExcelTypeEnum getExcelType();
}
