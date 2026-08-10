package ai.chat2db.community.domain.core.impl.task;

import ai.chat2db.community.domain.api.model.task.ExportAsyncContext;
import ai.chat2db.community.domain.api.service.task.ITaskDataExportService;
import ai.chat2db.community.domain.core.impl.task.export.ExportStrategyRegistry;
import ai.chat2db.community.domain.core.impl.task.export.IExportStrategy;
import org.springframework.stereotype.Service;

@Service
public class TaskDataExportServiceImpl implements ITaskDataExportService {

    private final ExportStrategyRegistry exportStrategyRegistry;

    public TaskDataExportServiceImpl(ExportStrategyRegistry exportStrategyRegistry) {
        this.exportStrategyRegistry = exportStrategyRegistry;
    }

    @Override
    public void exportOtherFile(ExportAsyncContext asyncContext) {
        IExportStrategy dataExportStrategy = exportStrategyRegistry.getExporter(asyncContext.getExportType());
        dataExportStrategy.run(asyncContext);
    }
}
