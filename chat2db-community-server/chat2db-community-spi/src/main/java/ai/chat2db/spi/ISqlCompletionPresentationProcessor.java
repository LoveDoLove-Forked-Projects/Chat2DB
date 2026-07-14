package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Converts completion pipeline output into the final response returned to callers.
 */
public interface ISqlCompletionPresentationProcessor {

    /**
     * Builds the final completion result from the pipeline state.
     *
     * @param state current completion pipeline state.
     * @return completion response containing candidates, hints, and trace data.
     * <p>
     * Typical usage:
     */
    SqlCompletionResponse process(SqlCompletionPipelineState state);
}
