package ai.chat2db.plugin.mysql.completion.intent;

import ai.chat2db.spi.parser.completion.AbstractSqlCompletionIntentResolver;
import ai.chat2db.spi.ISqlCompletionIntentRule;
import java.util.List;


public final class MysqlSqlCompletionIntentResolver extends AbstractSqlCompletionIntentResolver {

    private static final List<ISqlCompletionIntentRule> RULES = List.of(new MysqlSqlCompletionSlotIntentRule());

    @Override
    protected List<ISqlCompletionIntentRule> rules() {
        return RULES;
    }
}
