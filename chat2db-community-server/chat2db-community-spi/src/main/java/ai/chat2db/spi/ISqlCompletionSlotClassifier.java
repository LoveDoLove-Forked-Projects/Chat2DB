package ai.chat2db.spi;

import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.List;

/**
 * Classifies all completion slots that are relevant at the cursor position.
 */
public interface ISqlCompletionSlotClassifier {

    /**
     * Classifies the current cursor position into completion slots.
     *
     * @param state current completion pipeline state.
     * @return slots that apply to the current completion request.
     * <p>
     * Typical usage:
     */
    List<SqlCompletionSlot> classify(SqlCompletionPipelineState state);
}
