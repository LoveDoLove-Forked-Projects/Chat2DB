package ai.chat2db.community.domain.core.impl.task.export.excel;

import ai.chat2db.community.domain.api.enums.ExportFileSuffixEnum;
import com.alibaba.excel.support.ExcelTypeEnum;


public class XlsDataExporter extends BaseExcelExporter {

    public XlsDataExporter() {
        this.suffix = ExportFileSuffixEnum.XLS.getSuffix();
        this.contentType="application/vnd.ms-excel";
    }

    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLS;
    }
}
