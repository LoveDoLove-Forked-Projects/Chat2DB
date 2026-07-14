package ai.chat2db.community.domain.core.impl.task.imports.excel;

import ai.chat2db.community.domain.core.impl.task.imports.IImportStrategy;
import com.alibaba.excel.support.ExcelTypeEnum;


public class CSVImporter extends BaseExcelImporter implements IImportStrategy {


    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.CSV;
    }
}
