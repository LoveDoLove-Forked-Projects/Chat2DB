package ai.chat2db.spi.parser.completion;

import ai.chat2db.spi.ISqlCompletionSlotClassifier;
import ai.chat2db.spi.ISqlCompletionSlotRule;

import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSqlCompletionSlotClassifier implements ISqlCompletionSlotClassifier {

    @Override
    public final List<SqlCompletionSlot> classify(SqlCompletionPipelineState state) {
        return rules().stream()
                .map(rule -> rule.classify(state))
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .toList();
    }

    protected abstract List<ISqlCompletionSlotRule> rules();
}
