package ai.chat2db.plugin.mysql.completion.provider.table;

import ai.chat2db.plugin.mysql.completion.context.MysqlSqlCompletionLocalContextRelations;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.completion.provider.filter.MysqlSqlCompletionCompletedIdentifierFilter;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


final class MysqlSqlCompletionLocalTableCandidateProvider {

    private MysqlSqlCompletionLocalTableCandidateProvider() {
    }

    static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null
                || context.cursorContext().dotScoped()) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        List<SqlCompletionCandidate> candidates = MysqlSqlCompletionLocalContextRelations.relations(context).stream()
                .filter(MysqlSqlCompletionRelationScope.Relation::local)
                .map(MysqlSqlCompletionRelationScope.Relation::table)
                .filter(table -> StringUtils.startsWithIgnoreCase(table, tableReferencePrefix(context)))
                .filter(table -> !MysqlSqlCompletionCompletedIdentifierFilter.repeatsCompletedIdentifier(
                        context, table))
                .distinct()
                .map(MysqlSqlCompletionLocalTableCandidateProvider::candidate)
                .toList();
        return candidates.isEmpty()
                ? MysqlSqlCompletionCandidateBuildResult.empty()
                : MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static String tableReferencePrefix(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.cursorContext() == null) {
            return "";
        }
        SqlCompletionCursorContext cursorContext = context.cursorContext();
        return cursorContext.dotScoped() ? cursorContext.prefix() : context.prefix();
    }

    private static SqlCompletionCandidate candidate(String table) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.TABLE, table);
        candidate.setTableName(table);
        candidate.setObjectName(table);
        candidate.setSortRank(100);
        return candidate;
    }
}
