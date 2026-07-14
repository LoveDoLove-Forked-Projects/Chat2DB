package ai.chat2db.community.domain.api.service.storage;

/**
 * Resolves the active workspace storage implementation.
 */
public interface IWorkspaceStorageProviderResolver {

    /**
     * Resolves the workspace storage selected for the current runtime.
     *
     * @return resolved workspace storage.
     */
    IWorkspaceStorage resolve();
}
