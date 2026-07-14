package ai.chat2db.community.domain.core.impl.operation;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.operation.OperationLog;
import ai.chat2db.community.domain.api.model.request.operation.OpsOperationLogPageQueryRequest;
import ai.chat2db.community.domain.api.service.ops.IOpsOperationLogQueryService;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OpsOperationLogQueryServiceImpl implements IOpsOperationLogQueryService {

    private static final int DDL_PREVIEW_LENGTH = 200;

    private final IWorkspaceStorageFacade workspaceStorageFacade;

    public OpsOperationLogQueryServiceImpl(IWorkspaceStorageFacade workspaceStorageFacade) {
        this.workspaceStorageFacade = workspaceStorageFacade;
    }

    @Override
    public PageResponse<OperationLog> operationLogList(OpsOperationLogPageQueryRequest request) {
        return workspaceStorageFacade.operationLogList(request);
    }

    @Override
    public PageResponse<OperationLog> operationLogPreviewList(OpsOperationLogPageQueryRequest request) {
        PageResponse<OperationLog> page = operationLogList(request);
        if (CollectionUtils.isNotEmpty(page.getData())) {
            page.getData().stream().filter(Objects::nonNull).forEach(this::preparePreview);
        }
        return page;
    }

    @Override
    public OperationLog getOperationLog(Long id) {
        return workspaceStorageFacade.getOperationLog(id);
    }

    @Override
    public Long createOperationLog(OperationLog request) {
        return workspaceStorageFacade.createOperationLog(request);
    }

    private void preparePreview(OperationLog operationLog) {
        if (StringUtils.isBlank(operationLog.getDdl())) {
            return;
        }
        if (operationLog.getDdl().length() > DDL_PREVIEW_LENGTH) {
            operationLog.setDdl(operationLog.getDdl().substring(0, DDL_PREVIEW_LENGTH) + "...");
            operationLog.setMore(Boolean.TRUE);
        }
    }
}
