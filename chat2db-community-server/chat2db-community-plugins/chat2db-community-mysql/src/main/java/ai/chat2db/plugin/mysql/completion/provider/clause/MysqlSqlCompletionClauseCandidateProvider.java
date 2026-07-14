package ai.chat2db.plugin.mysql.completion.provider.clause;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.completion.catalog.MysqlSqlCompletionClauseCatalog;
import ai.chat2db.plugin.mysql.completion.catalog.MysqlSqlCompletionClauseRule;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;


public final class MysqlSqlCompletionClauseCandidateProvider {

    private MysqlSqlCompletionClauseCandidateProvider() {
    }

    public static List<SqlCompletionCandidate> fromToken(
            MysqlSqlCompletionCandidateContext context,
            MysqlSqlCompletionClauseEvidenceResolver evidenceResolver,
            Integer tokenType,
            List<Integer> ruleList,
            SqlCompletionCandidates c3Result) {
        if (context == null || tokenType == null || tokenType <= 0) {
            return List.of();
        }
        if (context.cursorContext() != null && context.cursorContext().dotScoped()) {
            return List.of();
        }
        MysqlSqlCompletionClauseEvidenceResolver resolver = evidenceResolver == null
                ? new MysqlSqlCompletionClauseEvidenceResolver(context)
                : evidenceResolver;
        return MysqlSqlCompletionClauseCatalog.clauses(tokenType).stream()
                .filter(rule -> rule.spec().matchesPrefix(context.prefix()))
                .filter(rule -> resolver.accepts(rule, ruleList, c3Result))
                .map(MysqlSqlCompletionClauseCandidateProvider::candidate)
                .toList();
    }

    private static SqlCompletionCandidate candidate(MysqlSqlCompletionClauseRule rule) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD,
                rule.spec().label());
        candidate.setInsertText(rule.spec().insertText());
        candidate.setDescription(rule.spec().description());
        candidate.setSortRank(rule.spec().sortRank());
        return candidate;
    }
}
