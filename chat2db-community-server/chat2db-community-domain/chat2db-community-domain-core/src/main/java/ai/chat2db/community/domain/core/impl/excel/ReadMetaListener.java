package ai.chat2db.community.domain.core.impl.excel;

import ai.chat2db.community.domain.api.model.excel.ExcelCheckResponse;
import ai.chat2db.community.domain.api.service.db.IDbExcelTableService;
import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadMetaListener implements ReadListener<Map<Integer, Object>> {

    private String filePath;

    private ExcelCheckResponse result;

    private IDbExcelTableService excelTableService;

    public ReadMetaListener(ExcelCheckResponse result, String filePath, IDbExcelTableService excelTableService) {
        this.result = result;
        this.filePath = filePath;
        this.excelTableService = excelTableService;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {

    }

    private ExcelCheckResponse.Sheet getSheet() {
        return result.getSheetList().get(result.getSheetList().size() - 1);
    }

    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        setSheet(context);
        data = excelTableService.dateTime2Str(data);
        List<Object> val = data.values().stream().toList();
        int rowNum = context.readRowHolder().getRowIndex();
        if (rowNum < 100) {
            getSheet().getDataList().add(val);
        }
    }

    private void setSheet(AnalysisContext context) {
        ExcelCheckResponse.Sheet sheet = new ExcelCheckResponse.Sheet();
        if (FileUtil.getSuffix(filePath).equalsIgnoreCase("csv")) {
            if(CollectionUtils.isNotEmpty(result.getSheetList())){
                return;
            }
            sheet.setSheetName(FileUtil.getName(filePath).toUpperCase());
            sheet.setSheetNo(0);
        } else {
            ReadSheetHolder readSheetHolder = context.readSheetHolder();
            sheet.setSheetName(readSheetHolder.getSheetName().toUpperCase());
            int sheetNo = readSheetHolder.getSheetNo();
            for (ExcelCheckResponse.Sheet s : result.getSheetList()) {
                if (s.getSheetNo().equals(sheetNo)) {
                    return;
                }
            }
            Integer rowNumber = readSheetHolder.getApproximateTotalRowNumber();
            sheet.setSheetNo(sheetNo);
            sheet.setRowNum(rowNumber == null ? 0 : rowNumber);
        }
        sheet.setTableName(sheet.getSheetName() + "_META_" + System.currentTimeMillis());
        sheet.setHeaderList(new ArrayList<>());
        sheet.setDataList(new ArrayList<>());
        result.getSheetList().add(sheet);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
