package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Normalizes raw editor input before SQL completion analysis.
 */
public interface ISqlCompletionInputCleaner {

    /**
     * Cleans editor input and records any cursor-offset changes.
     *
     * @param state current completion pipeline state.
     * @return cleaned input result used by later completion stages.
     * <p>
     * Typical usage:
     */
    SqlCompletionInputCleanResponse clean(SqlCompletionPipelineState state);
}
