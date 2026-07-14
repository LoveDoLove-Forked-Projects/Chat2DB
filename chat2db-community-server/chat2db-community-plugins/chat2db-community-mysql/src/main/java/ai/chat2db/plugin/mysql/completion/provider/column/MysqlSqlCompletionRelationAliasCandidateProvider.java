package ai.chat2db.plugin.mysql.completion.provider.column;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


final class MysqlSqlCompletionRelationAliasCandidateProvider {

    private MysqlSqlCompletionRelationAliasCandidateProvider() {
    }

    static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context,
                                                        MysqlSqlCompletionRelationScope relationScope) {
        if (context == null || context.cursorContext() == null || context.cursorContext().dotScoped()
                || relationScope == null) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        List<SqlCompletionCandidate> candidates = relationScope.relations().stream()
                .filter(relation -> StringUtils.isNotBlank(relation.alias()))
                .filter(relation -> StringUtils.startsWithIgnoreCase(relation.alias(), context.prefix()))
                .map(MysqlSqlCompletionRelationAliasCandidateProvider::candidate)
                .toList();
        if (candidates.isEmpty()) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static SqlCompletionCandidate candidate(MysqlSqlCompletionRelationScope.Relation relation) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.ALIAS,
                relation.alias());
        candidate.setInsertText(relation.alias() + ".");
        candidate.setDatabaseName(relation.catalog());
        candidate.setSchemaName(relation.schema());
        candidate.setTableName(relation.table());
        candidate.setTableAlias(relation.alias());
        candidate.setSortRank(50);
        return candidate;
    }
}
