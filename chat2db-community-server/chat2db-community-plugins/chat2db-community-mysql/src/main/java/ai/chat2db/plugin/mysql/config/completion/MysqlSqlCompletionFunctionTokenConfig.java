package ai.chat2db.plugin.mysql.config.completion;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.community.domain.api.config.completion.SqlCompletionRuleConfig;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LL1Analyzer;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionFunctionTokenConfig extends SqlCompletionRuleConfig {

    private static final Vocabulary VOCABULARY = MySqlLexer.VOCABULARY;
    private static final MysqlSqlCompletionFunctionTokenConfig INSTANCE =
            new MysqlSqlCompletionFunctionTokenConfig();
    private static final Set<Integer> NON_CANDIDATE_FUNCTION_TOKENS = Set.of(
            MySqlLexer.INVISIBLE,
            MySqlLexer.SESSION_VARIABLES_ADMIN,
            MySqlLexer.STATEMENT,
            MySqlLexer.VISIBLE);
    private static final Set<Integer> GRAMMAR_FUNCTION_TOKENS = grammarFunctionTokens();

    private MysqlSqlCompletionFunctionTokenConfig() {
        super(Set.of(
                MySqlParser.RULE_functionCall,
                MySqlParser.RULE_specificFunction,
                MySqlParser.RULE_aggregateWindowedFunction,
                MySqlParser.RULE_nonAggregateWindowedFunction,
                MySqlParser.RULE_scalarFunctionName,
                MySqlParser.RULE_passwordFunctionClause,
                MySqlParser.RULE_functionNameBase));
    }

    public static boolean isFunctionToken(Integer tokenType, List<Integer> ruleList) {
        return isFunctionToken(tokenType, ruleList, null);
    }

    public static boolean isFunctionToken(Integer tokenType, List<Integer> ruleList,
                                          SqlCompletionCandidates c3Result) {
        if (tokenType == null || tokenType <= 0) {
            return false;
        }
        if (isNonCandidateFunctionToken(tokenType, ruleList, c3Result)) {
            return false;
        }
        return (hasFunctionRule(ruleList) || hasCurrentFunctionRule(c3Result))
                && GRAMMAR_FUNCTION_TOKENS.contains(tokenType);
    }

    public static boolean isNonCandidateFunctionToken(Integer tokenType, List<Integer> ruleList) {
        return isNonCandidateFunctionToken(tokenType, ruleList, null);
    }

    public static boolean isNonCandidateFunctionToken(Integer tokenType, List<Integer> ruleList,
                                                      SqlCompletionCandidates c3Result) {
        if (tokenType == null || !NON_CANDIDATE_FUNCTION_TOKENS.contains(tokenType)) {
            return false;
        }
        return hasFunctionRule(ruleList) || hasCurrentFunctionRule(c3Result);
    }

    public static boolean hasFunctionRule(List<Integer> ruleList) {
        return INSTANCE.hasConfiguredRule(ruleList);
    }

    public static boolean hasFunctionRule(Integer rule) {
        return INSTANCE.hasConfiguredRule(rule);
    }

    public static boolean hasCurrentFunctionRule(SqlCompletionCandidates c3Result) {
        return INSTANCE.hasCurrentConfiguredRule(c3Result);
    }

    public static String tokenName(Integer tokenType) {
        if (tokenType == null || tokenType <= 0) {
            return null;
        }
        String literal = VOCABULARY.getLiteralName(tokenType);
        if (StringUtils.isBlank(literal) || literal.length() < 3
                || !literal.startsWith("'") || !literal.endsWith("'")) {
            return null;
        }
        String text = literal.substring(1, literal.length() - 1);
        return StringUtils.isBlank(text) ? null : text;
    }

    private static Set<Integer> grammarFunctionTokens() {
        Set<Integer> tokens = new LinkedHashSet<>();
        ATN atn = MySqlParser._ATN;
        LL1Analyzer analyzer = new LL1Analyzer(atn);
        addLookahead(tokens, analyzer, atn, MySqlParser.RULE_functionNameBase);
        addLookahead(tokens, analyzer, atn, MySqlParser.RULE_scalarFunctionName);
        addLookahead(tokens, analyzer, atn, MySqlParser.RULE_aggregateWindowedFunction);
        addLookahead(tokens, analyzer, atn, MySqlParser.RULE_nonAggregateWindowedFunction);
        addLookahead(tokens, analyzer, atn, MySqlParser.RULE_specificFunction);
        addLookahead(tokens, analyzer, atn, MySqlParser.RULE_passwordFunctionClause);
        return Set.copyOf(tokens);
    }

    private static void addLookahead(Set<Integer> tokens, LL1Analyzer analyzer, ATN atn, int rule) {
        IntervalSet lookahead = analyzer.LOOK(atn.ruleToStartState[rule], null);
        for (Integer token : lookahead.toList()) {
            if (token != null && token > 0) {
                tokens.add(token);
            }
        }
    }

}
