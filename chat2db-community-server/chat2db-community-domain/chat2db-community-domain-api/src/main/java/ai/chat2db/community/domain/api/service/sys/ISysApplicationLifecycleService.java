package ai.chat2db.community.domain.api.service.sys;

/**
 * Coordinates application lifecycle operations outside controller code.
 */
public interface ISysApplicationLifecycleService {

    /**
     * Returns the configured system UUID.
     *
     * @return system UUID.
     */
    String getSystemUuid();

    /**
     * Shuts down the CLI runtime process after the response has been returned.
     *
     * @return shutdown acknowledgement.
     */
    boolean shutdownCliRuntime();
}
