package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlan;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Builds the candidate generation plan for SQL completion.
 */
public interface ISqlCompletionCandidatePlanner {

    /**
     * Creates a candidate plan from the current pipeline state.
     *
     * @param state current completion pipeline state.
     * @return ordered plan that tells providers which candidates to generate.
     * <p>
     * Typical usage:
     */
    SqlCompletionCandidatePlan plan(SqlCompletionPipelineState state);
}
