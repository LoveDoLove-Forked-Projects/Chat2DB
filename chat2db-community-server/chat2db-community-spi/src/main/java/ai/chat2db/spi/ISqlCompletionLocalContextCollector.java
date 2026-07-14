package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalContext;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;


public interface ISqlCompletionLocalContextCollector {

    /**
     * Collects local tables, columns, aliases, variables, and other symbols visible near the cursor.
     *
     * @param state current completion pipeline state.
     * @return local completion context used by candidate providers.
     * <p>
     * Typical usage:
     */
    SqlCompletionLocalContext collect(SqlCompletionPipelineState state);
}
