package ai.chat2db.community.domain.core.impl.task.export.excel;

import ai.chat2db.community.domain.api.enums.ExportFileSuffixEnum;
import com.alibaba.excel.support.ExcelTypeEnum;


public class CsvDataExporter extends BaseExcelExporter {


    public CsvDataExporter() {
        this.contentType = "text/csv";
        this.suffix = ExportFileSuffixEnum.CSV.getSuffix();
    }


    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.CSV;
    }
}
