package ai.chat2db.plugin.mysql.completion.provider.snippet;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.completion.catalog.MysqlSqlCompletionSnippetCatalog;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionInsertTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionSnippetSpec;
import java.util.Comparator;
import java.util.List;
import org.antlr.v4.runtime.Token;


public final class MysqlSqlCompletionSnippetCandidateProvider {

    private MysqlSqlCompletionSnippetCandidateProvider() {
    }

    public static List<SqlCompletionCandidate> build(MysqlSqlCompletionCandidateContext context,
                                                     SqlCompletionCandidates c3Result) {
        if (context == null) {
            return List.of();
        }
        if (context.cursorContext() != null && context.cursorContext().dotScoped()) {
            return List.of();
        }
        return MysqlSqlCompletionSnippetCatalog.phrases().stream()
                .filter(spec -> accepts(context, c3Result, spec))
                .filter(spec -> isAtSnippetOpeningToken(context, c3Result, spec))
                .filter(spec -> spec.matchesPrefix(context.prefix()))
                .sorted(Comparator.comparingInt(SqlCompletionSnippetSpec::sortRank)
                        .thenComparing(SqlCompletionSnippetSpec::label))
                .map(MysqlSqlCompletionSnippetCandidateProvider::candidate)
                .toList();
    }

    private static boolean accepts(MysqlSqlCompletionCandidateContext context,
                                   SqlCompletionCandidates c3Result,
                                   SqlCompletionSnippetSpec spec) {
        return spec != null
                && c3Result != null
                && c3Result.available()
                && c3Result.tokens().containsKey(spec.tokenType());
    }

    private static boolean isAtSnippetOpeningToken(MysqlSqlCompletionCandidateContext context,
                                                   SqlCompletionCandidates c3Result,
                                                   SqlCompletionSnippetSpec spec) {
        if (spec == null) {
            return false;
        }
        if (isAtStatementOpeningToken(context)) {
            return spec.tokenType() != MySqlLexer.BEGIN;
        }
        return spec.tokenType() == MySqlLexer.BEGIN && isRoutineBodyRuleSlot(c3Result);
    }

    private static boolean isAtStatementOpeningToken(MysqlSqlCompletionCandidateContext context) {
        if (context.window() == null || context.cursorContext() == null) {
            return false;
        }
        Token firstToken = MysqlSqlCompletionTokenUtil.firstEffectiveDefaultToken(context.window().parseSql());
        return firstToken != null && firstToken.getStartIndex() == context.cursorContext().replaceStart();
    }

    private static boolean isRoutineBodyRuleSlot(SqlCompletionCandidates c3Result) {
        return c3Result != null
                && c3Result.rules().values().stream()
                .anyMatch(candidate -> candidate != null
                        && candidate.ruleList().contains(MySqlParser.RULE_routineBody));
    }

    private static SqlCompletionCandidate candidate(SqlCompletionSnippetSpec spec) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.SNIPPET,
                spec.label());
        candidate.setInsertText(spec.insertText());
        candidate.setInsertType(SqlCompletionInsertTypeEnum.SNIPPET);
        candidate.setDescription(spec.description());
        candidate.setSortRank(spec.sortRank());
        candidate.setSnippetSlots(spec.slots());
        return candidate;
    }
}
