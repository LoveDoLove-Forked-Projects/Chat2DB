package ai.chat2db.community.storage;

import ai.chat2db.community.tools.exception.storage.UnsupportedStorageCapabilityException;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorage;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageProvider;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageProviderResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultWorkspaceStorageProviderResolver implements IWorkspaceStorageProviderResolver {

    private final List<IWorkspaceStorageProvider> providers;

    public DefaultWorkspaceStorageProviderResolver(List<IWorkspaceStorageProvider> providers) {
        this.providers = providers;
    }

    @Override
    public IWorkspaceStorage resolve() {
        return providers.stream()
                .filter(IWorkspaceStorageProvider::support)
                .findFirst()
                .map(IWorkspaceStorageProvider::storage)
                .orElseThrow(() -> UnsupportedStorageCapabilityException.forCapability(
                        "workspace-storage"));
    }
}
