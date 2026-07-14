package ai.chat2db.plugin.mysql.completion.slot;

import ai.chat2db.plugin.mysql.completion.context.MysqlSqlCompletionCandidateContextFactory;
import ai.chat2db.spi.parser.completion.AbstractSqlCompletionSlotClassifier;
import ai.chat2db.spi.ISqlCompletionSlotRule;
import java.util.List;


public final class MysqlSqlCompletionSlotClassifier extends AbstractSqlCompletionSlotClassifier {

    private final List<ISqlCompletionSlotRule> rules;

    public MysqlSqlCompletionSlotClassifier(MysqlSqlCompletionCandidateContextFactory contextFactory) {
        this.rules = List.of(new MysqlSqlCompletionRuleSlotRule(contextFactory));
    }

    @Override
    protected List<ISqlCompletionSlotRule> rules() {
        return rules;
    }
}
