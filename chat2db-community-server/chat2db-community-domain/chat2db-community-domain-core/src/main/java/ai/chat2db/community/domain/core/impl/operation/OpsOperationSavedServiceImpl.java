package ai.chat2db.community.domain.core.impl.operation;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.operation.Operation;
import ai.chat2db.community.domain.api.model.request.operation.OpsOperationPageQueryRequest;
import ai.chat2db.community.domain.api.service.ops.IOpsOperationSavedService;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageFacade;
import org.springframework.stereotype.Service;

@Service
public class OpsOperationSavedServiceImpl implements IOpsOperationSavedService {

    private static final String TAB_OPENED = "y";

    private final IWorkspaceStorageFacade workspaceStorageFacade;

    public OpsOperationSavedServiceImpl(IWorkspaceStorageFacade workspaceStorageFacade) {
        this.workspaceStorageFacade = workspaceStorageFacade;
    }

    @Override
    public PageResponse<Operation> consoleList(OpsOperationPageQueryRequest request) {
        return workspaceStorageFacade.consoleList(request);
    }

    @Override
    public Operation getConsole(Long id) {
        return workspaceStorageFacade.getConsole(id);
    }

    @Override
    public Long createConsole(Operation request) {
        request.setTabOpened(TAB_OPENED);
        return workspaceStorageFacade.createConsole(request);
    }

    @Override
    public void updateConsole(Operation request) {
        workspaceStorageFacade.updateConsole(request);
    }

    @Override
    public void deleteConsole(Long id) {
        workspaceStorageFacade.deleteConsole(id);
    }

    @Override
    public void closeTabs() {
    }
}
