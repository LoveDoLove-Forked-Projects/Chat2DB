package ai.chat2db.community.storage;

import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorage;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageProvider;
import org.springframework.stereotype.Component;

@Component
public class LocalWorkspaceStorageProvider implements IWorkspaceStorageProvider {

    private final IWorkspaceStorage storage;

    public LocalWorkspaceStorageProvider(LocalWorkspaceStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean support() {
        return true;
    }

    @Override
    public IWorkspaceStorage storage() {
        return storage;
    }
}
