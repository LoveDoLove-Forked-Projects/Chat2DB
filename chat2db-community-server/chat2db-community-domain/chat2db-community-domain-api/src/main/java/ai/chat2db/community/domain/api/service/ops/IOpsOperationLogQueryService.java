package ai.chat2db.community.domain.api.service.ops;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.operation.OperationLog;
import ai.chat2db.community.domain.api.model.request.operation.OpsOperationLogPageQueryRequest;

/**
 * Queries persisted operation logs.
 */
public interface IOpsOperationLogQueryService {

    PageResponse<OperationLog> operationLogList(OpsOperationLogPageQueryRequest request);

    /**
     * Lists operation logs with preview fields prepared for UI display.
     *
     * @param request operation log query parameters.
     * @return paged operation logs.
     */
    PageResponse<OperationLog> operationLogPreviewList(OpsOperationLogPageQueryRequest request);

    OperationLog getOperationLog(Long id);

    Long createOperationLog(OperationLog request);
}
