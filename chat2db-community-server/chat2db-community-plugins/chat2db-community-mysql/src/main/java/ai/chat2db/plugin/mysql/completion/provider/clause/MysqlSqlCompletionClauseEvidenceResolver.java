package ai.chat2db.plugin.mysql.completion.provider.clause;

import ai.chat2db.plugin.mysql.completion.c3.MysqlSqlCompletionEngine;
import ai.chat2db.plugin.mysql.completion.catalog.MysqlSqlCompletionClauseRule;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class MysqlSqlCompletionClauseEvidenceResolver {

    private final MysqlSqlCompletionCandidateContext context;
    private final Map<Set<Integer>, SqlCompletionCandidates> boundaryC3Results = new HashMap<>();

    public MysqlSqlCompletionClauseEvidenceResolver(MysqlSqlCompletionCandidateContext context) {
        this.context = context;
    }

    boolean accepts(MysqlSqlCompletionClauseRule rule,
                    List<Integer> tokenRuleList,
                    SqlCompletionCandidates c3Result) {
        if (rule == null) {
            return false;
        }
        return hasRule(tokenRuleList, rule.rulePathRules())
                || hasCurrentC3RulePath(c3Result, rule.rulePathRules())
                || hasBoundaryC3RulePath(rule);
    }

    private boolean hasBoundaryC3RulePath(MysqlSqlCompletionClauseRule rule) {
        Set<Integer> preferredRules = rule.boundaryPreferredRules();
        if (context == null || preferredRules.isEmpty()) {
            return false;
        }
        SqlCompletionCandidates additional = boundaryC3Results.computeIfAbsent(preferredRules,
                rules -> MysqlSqlCompletionEngine.collect(context, rules));
        return hasCurrentC3RulePath(additional, rule.rulePathRules());
    }

    private static boolean hasCurrentC3RulePath(SqlCompletionCandidates c3Result, Iterable<Integer> rules) {
        if (c3Result == null || !c3Result.available() || c3Result.rules().isEmpty()) {
            return false;
        }
        int tokenIndex = c3Result.tokenIndex();
        return c3Result.rules().entrySet().stream()
                .anyMatch(entry -> {
                    SqlCompletionCandidates.RuleCandidate candidate = entry.getValue();
                    return candidate != null
                            && candidate.startTokenIndex() <= tokenIndex
                            && (hasRule(List.of(entry.getKey()), rules)
                            || hasRule(candidate.ruleList(), rules));
                });
    }

    private static boolean hasRule(List<Integer> ruleList, Iterable<Integer> rules) {
        if (ruleList == null || rules == null) {
            return false;
        }
        for (Integer expected : rules) {
            if (expected != null && ruleList.contains(expected)) {
                return true;
            }
        }
        return false;
    }
}
