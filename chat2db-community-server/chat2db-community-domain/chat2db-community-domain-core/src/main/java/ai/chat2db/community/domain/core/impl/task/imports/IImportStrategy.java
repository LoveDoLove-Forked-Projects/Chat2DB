package ai.chat2db.community.domain.core.impl.task.imports;

import ai.chat2db.community.domain.api.model.task.ImportAsyncContext;

public interface IImportStrategy {


    void run(ImportAsyncContext asyncContext);
}
