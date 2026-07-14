package ai.chat2db.community.domain.api.model.completion.core;

import java.util.List;
import java.util.Map;


public record SqlCompletionCandidates(boolean available,
                                    int tokenIndex,
                                    Map<Integer, List<Integer>> tokens,
                                    Map<Integer, RuleCandidate> rules) {

    public SqlCompletionCandidates {
        tokenIndex = Math.max(0, tokenIndex);
        tokens = tokens == null ? Map.of() : Map.copyOf(tokens);
        rules = rules == null ? Map.of() : Map.copyOf(rules);
    }

    public boolean empty() {
        return tokens.isEmpty() && rules.isEmpty();
    }

    public static SqlCompletionCandidates unavailable() {
        return new SqlCompletionCandidates(false, 0, Map.of(), Map.of());
    }

    public record RuleCandidate(int startTokenIndex,
                                int stopTokenIndex,
                                List<Integer> ruleList) {

        public RuleCandidate {
            stopTokenIndex = Math.max(startTokenIndex, stopTokenIndex);
            ruleList = ruleList == null ? List.of() : List.copyOf(ruleList);
        }
    }
}
