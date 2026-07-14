package ai.chat2db.plugin.mysql.completion.plan;

import ai.chat2db.spi.parser.completion.AbstractSqlCompletionCandidatePlanner;
import ai.chat2db.spi.ISqlCompletionPlanRule;
import java.util.List;


public final class MysqlSqlCompletionCandidatePlanner extends AbstractSqlCompletionCandidatePlanner {

    private static final List<ISqlCompletionPlanRule> RULES = List.of(new MysqlSqlCompletionIntentPlanRule());

    @Override
    protected List<ISqlCompletionPlanRule> rules() {
        return RULES;
    }
}
