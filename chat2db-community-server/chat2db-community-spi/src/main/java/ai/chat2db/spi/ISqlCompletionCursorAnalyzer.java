package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Analyzes the SQL editor cursor context before completion planning.
 */
public interface ISqlCompletionCursorAnalyzer {

    /**
     * Analyzes statement, token, and cursor state around the completion point.
     *
     * @param state current completion pipeline state.
     * @return cursor context used by later completion stages.
     * <p>
     * Typical usage:
     */
    SqlCompletionCursorContext analyze(SqlCompletionPipelineState state);
}
