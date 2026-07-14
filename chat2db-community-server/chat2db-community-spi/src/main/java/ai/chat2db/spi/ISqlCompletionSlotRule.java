package ai.chat2db.spi;

import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.Optional;

/**
 * Classifies one possible completion slot from the current pipeline state.
 */
@FunctionalInterface
public interface ISqlCompletionSlotRule {

    /**
     * Attempts to classify the cursor position into one completion slot.
     *
     * @param state current completion pipeline state.
     * @return classified slot when the rule matches; otherwise {@link Optional#empty()}.
     * <p>
     * Typical usage:
     */
    Optional<SqlCompletionSlot> classify(SqlCompletionPipelineState state);
}
