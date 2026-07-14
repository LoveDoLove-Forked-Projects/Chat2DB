package ai.chat2db.community.domain.api.service.cli;

import ai.chat2db.community.domain.api.model.cli.CliRuntimeCapabilities;
import ai.chat2db.community.domain.api.model.cli.CliRuntimeHealth;

/**
 * Exposes runtime status and capability metadata for CLI callers.
 */
public interface ICliRuntimeService {

    /**
     * Returns runtime health visible to CLI callers.
     *
     * @return CLI runtime health.
     */
    CliRuntimeHealth health();

    /**
     * Returns runtime capabilities visible to CLI callers.
     *
     * @return CLI runtime capabilities.
     */
    CliRuntimeCapabilities capabilities();
}
