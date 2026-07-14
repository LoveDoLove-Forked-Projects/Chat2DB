package ai.chat2db.spi.parser.completion;

import ai.chat2db.spi.ISqlCompletionIntentResolver;
import ai.chat2db.spi.ISqlCompletionIntentRule;

import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlCompletionIntentResolver implements ISqlCompletionIntentResolver {

    @Override
    public final List<SqlCompletionIntent> resolve(SqlCompletionPipelineState state) {
        List<SqlCompletionIntent> intents = new ArrayList<>();
        for (SqlCompletionSlot slot : state.slots()) {
            for (ISqlCompletionIntentRule rule : rules()) {
                List<SqlCompletionIntent> resolved = rule.resolve(state, slot);
                if (resolved != null && !resolved.isEmpty()) {
                    intents.addAll(resolved);
                }
            }
        }
        return List.copyOf(intents);
    }

    protected abstract List<ISqlCompletionIntentRule> rules();
}
