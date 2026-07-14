package ai.chat2db.community.domain.api.config.completion;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;

import java.util.List;
import java.util.Set;


public abstract class SqlCompletionRuleConfig {

    private final Set<Integer> rules;

    protected SqlCompletionRuleConfig(Set<Integer> rules) {
        this.rules = rules == null ? Set.of() : Set.copyOf(rules);
    }

    protected final boolean hasConfiguredRule(Integer rule) {
        return rule != null && rules.contains(rule);
    }

    protected final boolean hasConfiguredRule(List<Integer> ruleList) {
        return ruleList != null && ruleList.stream().anyMatch(this::hasConfiguredRule);
    }

    protected final boolean hasConfiguredRuleAtToken(SqlCompletionCandidates c3Result, int tokenIndex) {
        if (c3Result == null || !c3Result.available()) {
            return false;
        }
        boolean tokenRuleMatched = c3Result.tokenIndex() == tokenIndex
                && c3Result.tokens().values().stream().anyMatch(this::hasConfiguredRule);
        return tokenRuleMatched
                || c3Result.rules().entrySet().stream()
                .anyMatch(entry -> entry.getValue() != null
                        && entry.getValue().startTokenIndex() == tokenIndex
                        && (hasConfiguredRule(entry.getKey()) || hasConfiguredRule(entry.getValue().ruleList())));
    }

    protected final boolean hasCurrentConfiguredRule(SqlCompletionCandidates c3Result) {
        if (c3Result == null || !c3Result.available()) {
            return false;
        }
        int tokenIndex = c3Result.tokenIndex();
        return c3Result.rules().entrySet().stream()
                .anyMatch(entry -> entry.getValue().startTokenIndex() == tokenIndex
                        && (hasConfiguredRule(entry.getKey()) || hasConfiguredRule(entry.getValue().ruleList())));
    }
}
