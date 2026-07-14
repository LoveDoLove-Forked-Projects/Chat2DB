package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.pin.PinTable;
import ai.chat2db.community.domain.api.model.request.pin.DbTablePinRequest;
import ai.chat2db.community.domain.api.service.db.IDbTablePinService;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbTablePinServiceImpl implements IDbTablePinService {

    private final IWorkspaceStorageFacade workspaceStorageFacade;

    public DbTablePinServiceImpl(IWorkspaceStorageFacade workspaceStorageFacade) {
        this.workspaceStorageFacade = workspaceStorageFacade;
    }

    @Override
    public List<String> queryPinTables(DbTablePinRequest request) {
        return workspaceStorageFacade.queryPinTables(request);
    }

    @Override
    public void pinTable(PinTable request) {
        workspaceStorageFacade.pinTable(request);
    }

    @Override
    public void deletePinTable(PinTable request) {
        workspaceStorageFacade.deletePinTable(request);
    }
}
