package ai.chat2db.community.domain.api.service.ai;

import ai.chat2db.community.domain.api.model.request.ai.AiBusinessContextBuildRequest;

/**
 * Builds structured business context for AI requests.
 */
public interface IAiBusinessContextService {

    /**
     * Builds structured AI business context for a request.
     *
     * @param aiBusinessContextBuildRequest AI business context build parameters.
     * @return structured business context text.
     */
    String buildStructuredContext(AiBusinessContextBuildRequest aiBusinessContextBuildRequest);
}
