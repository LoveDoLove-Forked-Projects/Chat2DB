package ai.chat2db.community.domain.core.impl.task.imports;

import ai.chat2db.community.tools.exception.ParamBusinessException;
import ai.chat2db.community.domain.core.impl.task.imports.excel.CSVImporter;
import ai.chat2db.community.domain.core.impl.task.imports.excel.XLSImporter;
import ai.chat2db.community.domain.core.impl.task.imports.excel.XLSXImporter;
import ai.chat2db.community.domain.core.impl.task.imports.json.JSONImporter;
import ai.chat2db.community.domain.core.impl.task.imports.sql.SQLImporter;

import java.util.Map;
import java.util.Objects;

public class ImportFactory {

    private static final Map<String, IImportStrategy> exports = Map.of(
            "xls", new XLSImporter(),
            "xlsx", new XLSXImporter(),
            "csv", new CSVImporter(),
            "json", new JSONImporter(),
            "sql", new SQLImporter()
    );

    public static IImportStrategy get(String type) {
        IImportStrategy dataExportStrategy = exports.get(type.toLowerCase());
        if (Objects.isNull(dataExportStrategy)) {
            throw new ParamBusinessException(type);
        }
        return dataExportStrategy;
    }
}
