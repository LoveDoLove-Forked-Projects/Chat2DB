package ai.chat2db.community.domain.api.service.ops;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.operation.Operation;
import ai.chat2db.community.domain.api.model.request.operation.OpsOperationPageQueryRequest;

/**
 * Manages saved console operations.
 */
public interface IOpsOperationSavedService {

    PageResponse<Operation> consoleList(OpsOperationPageQueryRequest request);

    Operation getConsole(Long id);

    Long createConsole(Operation request);

    void updateConsole(Operation request);

    void deleteConsole(Long id);

    void closeTabs();
}
