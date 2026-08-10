package ai.chat2db.community.domain.core.impl.task.export;

import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;

public interface IExportStrategy {

    String type();

    void run(ExportAsyncContext asyncContext);

}
