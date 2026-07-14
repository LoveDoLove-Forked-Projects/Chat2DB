package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.List;

/**
 * Resolves completion intents for one classified completion slot.
 */
@FunctionalInterface
public interface ISqlCompletionIntentRule {

    /**
     * Resolves intents produced by the given slot.
     *
     * @param state current completion pipeline state.
     * @param slot classified slot at the cursor position.
     * @return intents produced by the slot rule.
     * <p>
     * Typical usage:
     */
    List<SqlCompletionIntent> resolve(SqlCompletionPipelineState state, SqlCompletionSlot slot);
}
