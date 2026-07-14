package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Collects C3 completion candidates from a completion pipeline state.
 */
public interface ISqlCompletionCollector {

    /**
     * Collects completion candidates for the current cursor context.
     *
     * @param state current completion pipeline state.
     * @return candidate collection produced by the C3 engine.
     * <p>
     * Typical usage:
     */
    SqlCompletionCandidates collect(SqlCompletionPipelineState state);
}
