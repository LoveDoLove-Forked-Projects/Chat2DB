package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Builds dummy SQL used to parse incomplete editor input during completion.
 */
public interface ISqlCompletionDummyBuilder {

    /**
     * Builds dummy SQL for the current incomplete input.
     *
     * @param state current completion pipeline state.
     * @return dummy SQL and cursor mapping used by parser-based completion stages.
     * <p>
     * Typical usage:
     */
    SqlCompletionDummySql build(SqlCompletionPipelineState state);
}
