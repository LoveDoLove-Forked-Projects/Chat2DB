package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;
import ai.chat2db.community.tools.model.Context;

import java.util.List;

/**
 * Creates file-backed async task contexts.
 */
public interface ITaskFileService {

    String defaultExportPath();

    String resolveTaskName(String databaseName, String schemaName, List<String> tableNames);

    AsyncContext createSqlExportContext(Long taskId, String exportPath, String databaseName, String schemaName,
            List<String> tableNames, boolean containData, ITaskAsyncCall asyncCall, Context context);

    ExportAsyncContext createOtherExportContext(Long taskId, String exportPath, String databaseName, String schemaName,
            List<String> tableNames, String exportType, Boolean containsHeader, ITaskAsyncCall asyncCall,
            Context context);

    ImportAsyncContext createImportContext(Long taskId, String importType, String tableName, String fileName,
            ITaskAsyncCall asyncCall, Context context);
}
