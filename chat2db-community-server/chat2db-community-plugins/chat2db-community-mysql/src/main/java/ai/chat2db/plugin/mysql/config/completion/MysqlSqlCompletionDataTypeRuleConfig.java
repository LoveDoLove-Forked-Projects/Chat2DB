package ai.chat2db.plugin.mysql.config.completion;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.community.domain.api.config.completion.SqlCompletionRuleConfig;
import ai.chat2db.mysql.parser.base.MySqlParser;
import java.util.List;
import java.util.Set;


public final class MysqlSqlCompletionDataTypeRuleConfig extends SqlCompletionRuleConfig {

    private static final MysqlSqlCompletionDataTypeRuleConfig INSTANCE =
            new MysqlSqlCompletionDataTypeRuleConfig();

    private MysqlSqlCompletionDataTypeRuleConfig() {
        super(Set.of(
                MySqlParser.RULE_dataType,
                MySqlParser.RULE_dataTypeBase));
    }

    public static boolean hasDataTypeRule(List<Integer> ruleList) {
        return INSTANCE.hasConfiguredRule(ruleList);
    }

    public static boolean hasDataTypeRuleAtToken(SqlCompletionCandidates c3Result, int tokenIndex) {
        return INSTANCE.hasConfiguredRuleAtToken(c3Result, tokenIndex);
    }
}
