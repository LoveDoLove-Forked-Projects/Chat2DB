package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlanItem;
import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;
import java.util.Optional;

/**
 * Converts one completion intent into an optional candidate plan item.
 */
@FunctionalInterface
public interface ISqlCompletionPlanRule {

    /**
     * Plans candidate generation for a completion intent.
     *
     * @param state current completion pipeline state.
     * @param intent intent to plan.
     * @return plan item when the rule supports the intent; otherwise {@link Optional#empty()}.
     * <p>
     * Typical usage:
     */
    Optional<SqlCompletionCandidatePlanItem> plan(SqlCompletionPipelineState state, SqlCompletionIntent intent);
}
