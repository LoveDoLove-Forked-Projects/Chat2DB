package ai.chat2db.community.domain.api.service.storage;

/**
 * Supplies a workspace storage implementation when supported by the runtime.
 */
public interface IWorkspaceStorageProvider {

    /**
     * Checks whether this provider can supply workspace storage in the current runtime.
     *
     * @return true when the provider is available; false otherwise.
     */
    boolean support();

    /**
     * Returns the workspace storage supplied by this provider.
     *
     * @return workspace storage instance.
     */
    IWorkspaceStorage storage();
}
