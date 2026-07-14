package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

/**
 * Locates the SQL statement window that contains the completion cursor.
 */
public interface ISqlCompletionStatementLocator {

    /**
     * Finds the statement window used by completion analysis.
     *
     * @param state current completion pipeline state.
     * @return statement window around the cursor.
     * <p>
     * Typical usage:
     */
    SqlCompletionStatementWindow locate(SqlCompletionPipelineState state);
}
