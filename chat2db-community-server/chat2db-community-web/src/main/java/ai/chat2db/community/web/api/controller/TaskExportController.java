package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.service.task.ITaskTransferService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.task.TaskWebConverter;
import ai.chat2db.community.web.api.model.request.task.OtherFileExportRequest;
import ai.chat2db.community.web.api.model.request.task.SqlFileExportRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles export task creation for SQL and tabular data.
 */
@ConnectionInfoAspect
@RequestMapping("/api/export")
@RestController
@Slf4j
public class TaskExportController {

    private final ITaskTransferService taskTransferService;
    private final TaskWebConverter taskWebConverter;

    public TaskExportController(ITaskTransferService taskTransferService,
            TaskWebConverter taskWebConverter) {
        this.taskTransferService = taskTransferService;
        this.taskWebConverter = taskWebConverter;
    }

    /**
     * Handles SQL file for export tasks.
     * <p>
     * Endpoint: {@code POST /api/export/sql_file}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing long.
     */
    @PostMapping("/sql_file")
    public DataResult<Long> sqlFile(@Valid @RequestBody SqlFileExportRequest request) {
        return DataResult.of(taskTransferService.exportSqlFile(taskWebConverter.sqlFileExport2param(request)));
    }


    /**
     * Handles other file for export tasks.
     * <p>
     * Endpoint: {@code POST /api/export/other_file}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing long.
     */
    @PostMapping("/other_file")
    public DataResult<Long> otherFile(@Valid @RequestBody OtherFileExportRequest request) {
        return DataResult.of(taskTransferService.exportOtherFile(taskWebConverter.otherFileExport2param(request)));
    }
}
