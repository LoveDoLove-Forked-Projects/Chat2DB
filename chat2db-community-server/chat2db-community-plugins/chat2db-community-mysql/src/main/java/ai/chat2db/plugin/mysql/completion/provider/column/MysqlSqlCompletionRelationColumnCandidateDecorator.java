package ai.chat2db.plugin.mysql.completion.provider.column;

import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


final class MysqlSqlCompletionRelationColumnCandidateDecorator {

    private MysqlSqlCompletionRelationColumnCandidateDecorator() {
    }

    static List<SqlCompletionCandidate> decorate(List<SqlCompletionCandidate> candidates,
                                                 MysqlSqlCompletionRelationScope.Relation relation,
                                                 boolean qualifyInsertText) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        candidates.forEach(candidate -> decorate(candidate, relation, qualifyInsertText));
        return candidates;
    }

    static SqlCompletionCandidate decorate(SqlCompletionCandidate candidate,
                                           MysqlSqlCompletionRelationScope.Relation relation,
                                           boolean qualifyInsertText) {
        if (candidate == null || relation == null) {
            return candidate;
        }
        if (StringUtils.isBlank(candidate.getTableName())) {
            candidate.setTableName(relation.table());
        }
        if (StringUtils.isNotBlank(relation.alias())) {
            candidate.setTableAlias(relation.alias());
        }
        if (qualifyInsertText) {
            applyUnqualifiedInsertText(candidate, relation);
        }
        return candidate;
    }

    private static void applyUnqualifiedInsertText(SqlCompletionCandidate candidate,
                                                   MysqlSqlCompletionRelationScope.Relation relation) {
        if (StringUtils.isBlank(relation.alias())) {
            return;
        }
        String column = StringUtils.defaultIfBlank(candidate.getColumnName(), candidate.getLabel());
        if (StringUtils.isBlank(column) || StringUtils.contains(candidate.getInsertText(), ".")) {
            return;
        }
        candidate.setInsertText(relation.alias() + "." + column);
    }
}
