package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.db.ImportPreCheck;
import ai.chat2db.community.domain.api.model.task.CsvOptions;
import ai.chat2db.community.domain.api.model.task.ExcelOptions;
import ai.chat2db.community.domain.api.model.task.ImportColumnMapping;
import ai.chat2db.community.domain.api.model.task.JsonOptions;

import java.io.File;
import java.util.List;

/** Pre-flight checks for the import wizard: row count, sample conflicts and type mismatches. */
public interface IDbImportPreCheckService {

    ImportPreCheck analyze(Long dataSourceId, String databaseName, String schemaName, String tableName,
            File file, CsvOptions csvOptions, ExcelOptions excelOptions, JsonOptions jsonOptions,
            List<ImportColumnMapping> mappings);
}
