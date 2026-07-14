package ai.chat2db.plugin.mysql.completion.provider.column;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionLocalColumnScope;
import ai.chat2db.plugin.mysql.completion.resolver.MysqlSqlCompletionLocalColumnScopeResolver;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;


final class MysqlSqlCompletionLocalColumnCandidateProvider {

    private MysqlSqlCompletionLocalColumnCandidateProvider() {
    }

    static Optional<MysqlSqlCompletionCandidateBuildResult> build(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null
                || context.cursorContext().dotScoped()) {
            return Optional.empty();
        }
        MysqlSqlCompletionLocalColumnScope scope = MysqlSqlCompletionLocalColumnScopeResolver.resolve(
                context.window().parseSql(), context.cursorContext().replaceStart());
        if (!scope.applies()) {
            return Optional.empty();
        }
        String prefix = context.prefix();
        List<SqlCompletionCandidate> candidates = scope.columns().stream()
                .filter(column -> matchesPrefix(column, prefix))
                .map(column -> candidate(column, scope.table()))
                .toList();
        return Optional.of(MysqlSqlCompletionCandidateBuildResult.success(candidates));
    }

    private static boolean matchesPrefix(String column, String prefix) {
        return StringUtils.isBlank(prefix) || StringUtils.startsWithIgnoreCase(column, prefix);
    }

    private static SqlCompletionCandidate candidate(String column, String table) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.COLUMN, column);
        candidate.setColumnName(column);
        candidate.setTableName(table);
        candidate.setSortRank(200);
        return candidate;
    }
}
