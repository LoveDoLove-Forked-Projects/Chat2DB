package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.er.ERPosition;
import ai.chat2db.community.domain.api.service.db.IDbErPositionService;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageFacade;
import org.springframework.stereotype.Service;

@Service
public class DbErPositionServiceImpl implements IDbErPositionService {

    private final IWorkspaceStorageFacade workspaceStorageFacade;

    public DbErPositionServiceImpl(IWorkspaceStorageFacade workspaceStorageFacade) {
        this.workspaceStorageFacade = workspaceStorageFacade;
    }

    @Override
    public String getErPosition(Long dataSourceId, String databaseName, String schemaName) {
        return workspaceStorageFacade.getErPosition(dataSourceId, databaseName, schemaName);
    }

    @Override
    public void savePosition(ERPosition request) {
        workspaceStorageFacade.savePosition(request);
    }
}
