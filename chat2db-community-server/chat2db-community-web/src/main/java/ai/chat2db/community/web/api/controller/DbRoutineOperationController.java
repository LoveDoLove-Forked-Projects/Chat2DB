package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.sql.SqlPreview;
import ai.chat2db.community.domain.api.service.db.IDbRoutineOperationService;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.domain.api.service.ops.IOpsSqlOperationLogService;
import ai.chat2db.community.domain.api.enums.operation.SqlOperationLogSourceEnum;
import ai.chat2db.community.web.api.converter.db.DbWebConverter;
import ai.chat2db.community.web.api.model.request.db.RoutineMigrationRequest;
import ai.chat2db.community.web.api.model.request.db.RoutineOperationRequest;
import ai.chat2db.community.web.api.model.response.db.ExecuteResultResponse;
import ai.chat2db.community.web.api.model.response.db.SqlPreviewResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Previews and executes routine migration and invocation operations.
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/routine")
@RestController
public class DbRoutineOperationController {

    @Autowired
    private IDbRoutineOperationService routineOperationService;

    @Autowired
    private IOpsSqlOperationLogService sqlOperationLogRecorder;

    @Autowired
    private DbWebConverter dbWebConverter;

    /**
     * Previews invocation.
     * <p>
     * Endpoint: {@code POST /api/rdb/routine/preview_invocation}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL preview response.
     */
    @PostMapping("/preview_invocation")
    public DataResult<SqlPreviewResponse> previewInvocation(@Valid @RequestBody RoutineOperationRequest request) {
        SqlPreview preview = routineOperationService.previewInvocation(dbWebConverter.request2param(request));
        return DataResult.of(dbWebConverter.dto2response(preview));
    }

    /**
     * Previews migration.
     * <p>
     * Endpoint: {@code POST /api/rdb/routine/preview_migration}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL preview response.
     */
    @PostMapping("/preview_migration")
    public DataResult<SqlPreviewResponse> previewMigration(@Valid @RequestBody RoutineMigrationRequest request) {
        SqlPreview preview = routineOperationService.previewMigration(dbWebConverter.request2param(request));
        return DataResult.of(dbWebConverter.dto2response(preview));
    }

    /**
     * Executes migration.
     * <p>
     * Endpoint: {@code POST /api/rdb/routine/execute_migration}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing execute result response.
     */
    @PostMapping("/execute_migration")
    public DataResult<ExecuteResultResponse> executeMigration(@Valid @RequestBody RoutineMigrationRequest request) {
        ExecuteResponse executeResult = routineOperationService.executeMigration(dbWebConverter.request2param(request));
        ExecuteResultResponse result = dbWebConverter.dto2response(executeResult);
        sqlOperationLogRecorder.recordResultAsync(dbWebConverter.response2dto(result),
                SqlOperationLogSourceEnum.SQL_EDITOR_HTTP.name());
        return DataResult.of(result);
    }
}
