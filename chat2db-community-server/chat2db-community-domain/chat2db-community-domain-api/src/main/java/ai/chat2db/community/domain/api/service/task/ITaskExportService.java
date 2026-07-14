package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.community.domain.api.model.request.task.TaskSqlFileExportRequest;

public interface ITaskExportService {

    /**
     * Exports SQL file content through an async context.
     *
     * @param taskSqlFileExportRequest SQL file export parameters.
     * @param asyncContext async execution context.
     */
    void exportSqlFile(TaskSqlFileExportRequest taskSqlFileExportRequest, AsyncContext asyncContext);
}
