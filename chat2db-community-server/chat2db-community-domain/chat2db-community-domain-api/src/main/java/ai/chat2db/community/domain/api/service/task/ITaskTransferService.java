package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.request.task.TaskFileImportRequest;
import ai.chat2db.community.domain.api.model.request.task.TaskOtherFileExportRequest;
import ai.chat2db.community.domain.api.model.request.task.TaskSqlFileExportRequest;

/**
 * Creates and schedules file import/export tasks.
 */
public interface ITaskTransferService {

    Long exportSqlFile(TaskSqlFileExportRequest request);

    Long exportOtherFile(TaskOtherFileExportRequest request);

    Long importSqlFile(TaskFileImportRequest request);

    Long importOtherFile(TaskFileImportRequest request);
}
