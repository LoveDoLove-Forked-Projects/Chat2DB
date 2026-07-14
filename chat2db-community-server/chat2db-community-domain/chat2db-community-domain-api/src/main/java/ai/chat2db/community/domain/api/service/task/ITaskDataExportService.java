package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;

public interface ITaskDataExportService {

    /**
     * Exports non-SQL task data through the supplied async context.
     *
     * @param asyncContext export async context.
     */
    void exportOtherFile(ExportAsyncContext asyncContext);
}
