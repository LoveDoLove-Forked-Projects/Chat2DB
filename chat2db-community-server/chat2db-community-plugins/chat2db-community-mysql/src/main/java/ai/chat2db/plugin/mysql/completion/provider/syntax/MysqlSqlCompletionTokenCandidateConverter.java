package ai.chat2db.plugin.mysql.completion.provider.syntax;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.config.completion.MysqlSqlCompletionDataTypeRuleConfig;
import ai.chat2db.plugin.mysql.config.completion.MysqlSqlCompletionFunctionTokenConfig;
import ai.chat2db.plugin.mysql.completion.provider.clause.MysqlSqlCompletionClauseCandidateProvider;
import ai.chat2db.plugin.mysql.completion.provider.clause.MysqlSqlCompletionClauseEvidenceResolver;
import ai.chat2db.plugin.mysql.completion.provider.function.MysqlSqlCompletionFunctionCandidateProvider;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionTokenCandidateConverter {

    private static final Vocabulary VOCABULARY = MySqlLexer.VOCABULARY;

    private MysqlSqlCompletionTokenCandidateConverter() {
    }

    public static List<SqlCompletionCandidate> convert(MysqlSqlCompletionCandidateContext context,
                                                       SqlCompletionCandidates c3Result) {
        if (context == null || c3Result == null || !c3Result.available()) {
            return List.of();
        }
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        MysqlSqlCompletionClauseEvidenceResolver clauseEvidenceResolver =
                new MysqlSqlCompletionClauseEvidenceResolver(context);
        for (Map.Entry<Integer, List<Integer>> entry : c3Result.tokens().entrySet()) {
            for (SqlCompletionCandidate candidate : candidates(context, clauseEvidenceResolver, entry.getKey(),
                    entry.getValue(), c3Result)) {
                if (candidate == null || !matchesPrefix(candidate, context.prefix())) {
                    continue;
                }
                if (candidate.getSortRank() == null || candidate.getType() != SqlCompletionCandidateTypeEnum.KEYWORD) {
                    candidate.setSortRank(1000 + entry.getKey());
                }
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private static boolean matchesPrefix(SqlCompletionCandidate candidate, String prefix) {
        if (phraseCandidate(candidate)) {
            return true;
        }
        return StringUtils.isBlank(prefix) || StringUtils.startsWithIgnoreCase(candidate.getLabel(), prefix);
    }

    private static boolean phraseCandidate(SqlCompletionCandidate candidate) {
        return candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                && StringUtils.isNotBlank(candidate.getInsertText())
                && !StringUtils.equals(candidate.getLabel(), candidate.getInsertText());
    }

    private static List<SqlCompletionCandidate> candidates(
            MysqlSqlCompletionCandidateContext context,
            MysqlSqlCompletionClauseEvidenceResolver clauseEvidenceResolver,
            Integer tokenType,
            List<Integer> ruleList,
            SqlCompletionCandidates c3Result) {
        if (tokenType == null || tokenType <= 0) {
            return List.of();
        }
        List<SqlCompletionCandidate> phrases = MysqlSqlCompletionClauseCandidateProvider.fromToken(context,
                clauseEvidenceResolver, tokenType, ruleList, c3Result);
        if (!phrases.isEmpty()) {
            return phrases;
        }
        if (MysqlSqlCompletionFunctionTokenConfig.isFunctionToken(tokenType, ruleList, c3Result)) {
            SqlCompletionCandidate function = functionCandidate(tokenType, ruleList, c3Result);
            return function == null ? List.of() : List.of(function);
        }
        if (MysqlSqlCompletionDataTypeRuleConfig.hasDataTypeRule(ruleList)) {
            return List.of();
        }
        if (MysqlSqlCompletionFunctionTokenConfig.isNonCandidateFunctionToken(tokenType, ruleList, c3Result)) {
            return List.of();
        }
        String keyword = keyword(tokenType);
        if (StringUtils.isBlank(keyword)) {
            return List.of();
        }
        return List.of(SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, keyword));
    }

    private static SqlCompletionCandidate functionCandidate(Integer tokenType,
                                                            List<Integer> ruleList,
                                                            SqlCompletionCandidates c3Result) {
        return MysqlSqlCompletionFunctionCandidateProvider.fromToken(tokenType, ruleList, c3Result);
    }

    private static String keyword(Integer tokenType) {
        String literal = VOCABULARY.getLiteralName(tokenType);
        if (StringUtils.isBlank(literal) || literal.length() < 3
                || !literal.startsWith("'") || !literal.endsWith("'")) {
            return null;
        }
        String text = literal.substring(1, literal.length() - 1);
        if (StringUtils.isBlank(text) || !StringUtils.isAllUpperCase(text.replace("_", ""))) {
            return null;
        }
        return text;
    }
}
