package ai.chat2db.community.domain.core.impl.task.export;

import ai.chat2db.community.tools.exception.ParamBusinessException;
import ai.chat2db.community.domain.core.impl.task.export.excel.CsvDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.excel.XlsDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.excel.XlsxDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.json.JsonDataExporter;
import ai.chat2db.community.domain.core.impl.task.export.sql.SqlDataExporter;

import java.util.Map;
import java.util.Objects;

public class ExportFactory {
    private static final Map<String, IExportStrategy> exports = Map.of(
            "xls", new XlsDataExporter(),
            "xlsx", new XlsxDataExporter(),
            "csv", new CsvDataExporter(),
            "json", new JsonDataExporter(),
            "sql", new SqlDataExporter()
    );

    public static IExportStrategy getExporter(String type) {
        IExportStrategy dataExportStrategy = exports.get(type.toLowerCase());
        if (Objects.isNull(dataExportStrategy)) {
            throw new ParamBusinessException(type);
        }
        return dataExportStrategy;
    }


}
