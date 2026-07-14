package ai.chat2db.spi.parser.completion;

import ai.chat2db.spi.ISqlCompletionCandidatePlanner;
import ai.chat2db.spi.ISqlCompletionPlanRule;

import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlan;
import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlanItem;
import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import java.util.List;
import java.util.Objects;

public abstract class AbstractSqlCompletionCandidatePlanner implements ISqlCompletionCandidatePlanner {

    @Override
    public final SqlCompletionCandidatePlan plan(SqlCompletionPipelineState state) {
        List<SqlCompletionCandidatePlanItem> items = state.intents().stream()
                .map(intent -> planItem(state, intent))
                .filter(Objects::nonNull)
                .toList();
        return new SqlCompletionCandidatePlan(items);
    }

    protected abstract List<ISqlCompletionPlanRule> rules();

    private SqlCompletionCandidatePlanItem planItem(SqlCompletionPipelineState state, SqlCompletionIntent intent) {
        for (ISqlCompletionPlanRule rule : rules()) {
            SqlCompletionCandidatePlanItem item = rule.plan(state, intent).orElse(null);
            if (item != null) {
                return item;
            }
        }
        return null;
    }
}
