package ai.chat2db.plugin.mysql.completion.catalog;

import ai.chat2db.community.domain.api.model.completion.SqlCompletionClauseSpec;
import java.util.Set;

public record MysqlSqlCompletionClauseRule(int tokenType,
                                           SqlCompletionClauseSpec spec,
                                           Set<Integer> rulePathRules,
                                           Set<Integer> boundaryPreferredRules) {

    public MysqlSqlCompletionClauseRule {
        rulePathRules = rulePathRules == null ? Set.of() : Set.copyOf(rulePathRules);
        boundaryPreferredRules = boundaryPreferredRules == null ? Set.of() : Set.copyOf(boundaryPreferredRules);
    }
}
