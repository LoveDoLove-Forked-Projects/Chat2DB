package ai.chat2db.community.domain.core.impl.task.export.excel;

import ai.chat2db.community.domain.api.enums.ExportFileSuffixEnum;
import com.alibaba.excel.support.ExcelTypeEnum;


public class XlsxDataExporter extends BaseExcelExporter {

    public XlsxDataExporter() {
        this.suffix = ExportFileSuffixEnum.EXCEL.getSuffix();
        this.contentType="application/vnd.ms-excel";
    }


    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLSX;
    }
}
