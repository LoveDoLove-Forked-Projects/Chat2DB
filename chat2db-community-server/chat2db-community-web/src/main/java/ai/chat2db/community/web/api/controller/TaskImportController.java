package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.service.task.ITaskTransferService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.task.TaskWebConverter;
import ai.chat2db.community.web.api.model.request.task.OtherFileImportRequest;
import ai.chat2db.community.web.api.model.request.task.SqlFileImportRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles SQL and structured-file import endpoints.
 */
@ConnectionInfoAspect
@RequestMapping("/api/import")
@RestController
@Slf4j
public class TaskImportController {

    private final ITaskTransferService taskTransferService;
    private final TaskWebConverter taskWebConverter;

    public TaskImportController(ITaskTransferService taskTransferService,
            TaskWebConverter taskWebConverter) {
        this.taskTransferService = taskTransferService;
        this.taskWebConverter = taskWebConverter;
    }

    /**
     * Handles SQL file for import tasks.
     * <p>
     * Endpoint: {@code POST /api/import/sql_file}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing long.
     */
    @PostMapping("/sql_file")
    public DataResult<Long> sqlFile(@Valid @RequestBody SqlFileImportRequest request) {
        return DataResult.of(taskTransferService.importSqlFile(taskWebConverter.sqlFileImport2param(request)));
    }

    /**
     * Handles other file for import tasks.
     * <p>
     * Endpoint: {@code POST /api/import/other_file}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing long.
     */
    @PostMapping("/other_file")
    public DataResult<Long> otherFile(@Valid @RequestBody OtherFileImportRequest request) {
        return DataResult.of(taskTransferService.importOtherFile(taskWebConverter.otherFileImport2param(request)));
    }
}
