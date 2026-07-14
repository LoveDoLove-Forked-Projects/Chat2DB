package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import java.util.List;

/**
 * Resolves completion intents from the analyzed SQL completion state.
 */
public interface ISqlCompletionIntentResolver {

    /**
     * Resolves all completion intents that apply at the current cursor position.
     *
     * @param state current completion pipeline state.
     * @return ordered intents to be consumed by the candidate planner.
     * <p>
     * Typical usage:
     */
    List<SqlCompletionIntent> resolve(SqlCompletionPipelineState state);
}
