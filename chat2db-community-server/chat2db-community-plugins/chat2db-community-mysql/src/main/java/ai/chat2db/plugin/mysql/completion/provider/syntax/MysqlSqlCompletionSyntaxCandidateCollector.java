package ai.chat2db.plugin.mysql.completion.provider.syntax;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class MysqlSqlCompletionSyntaxCandidateCollector {

    private MysqlSqlCompletionSyntaxCandidateCollector() {
    }

    public static List<SqlCompletionCandidate> collect(MysqlSqlCompletionCandidateContext context,
                                                       SqlCompletionCandidates c3Result) {
        Map<String, SqlCompletionCandidate> unique = new LinkedHashMap<>();
        putSyntaxCandidates(unique, MysqlSqlCompletionTokenCandidateConverter.convert(context, c3Result));
        return List.copyOf(unique.values());
    }

    private static void putSyntaxCandidates(Map<String, SqlCompletionCandidate> unique,
                                            List<SqlCompletionCandidate> candidates) {
        for (SqlCompletionCandidate candidate : candidates) {
            if (!isSyntaxCandidate(candidate)) {
                continue;
            }
            unique.putIfAbsent(candidate.getType() + "|" + candidate.getLabel(), candidate);
        }
    }

    private static boolean isSyntaxCandidate(SqlCompletionCandidate candidate) {
        return candidate != null
                && (candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                || candidate.getType() == SqlCompletionCandidateTypeEnum.FUNCTION
                || candidate.getType() == SqlCompletionCandidateTypeEnum.SNIPPET);
    }

}
