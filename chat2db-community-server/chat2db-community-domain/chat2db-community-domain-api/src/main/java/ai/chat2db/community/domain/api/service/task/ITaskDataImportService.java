package ai.chat2db.community.domain.api.service.task;

import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;

public interface ITaskDataImportService {

    /**
     * Imports non-SQL task data through the supplied async context.
     *
     * @param asyncContext import async context.
     */
    void importOtherFile(ImportAsyncContext asyncContext);
}
