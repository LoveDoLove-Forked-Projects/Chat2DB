package ai.chat2db.community.domain.api.service.ai;

/**
 * Streams AI chat responses for a transport-specific request and response type.
 *
 */
public interface IAiChatStreamService<Q, R> {

    /**
     * Streams an AI chat response.
     *
     * @param request chat request.
     * @return streaming response.
     */
    R stream(Q request);
}
