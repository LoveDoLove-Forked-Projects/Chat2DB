package ai.chat2db.plugin.mysql.completion.provider.column;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionInsertColumnListContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;


import static ai.chat2db.plugin.mysql.constant.MysqlSqlCompletionInsertColumnListCandidateFilterConstants.*;
final class MysqlSqlCompletionInsertColumnListCandidateFilter {


    private MysqlSqlCompletionInsertColumnListCandidateFilter() {
    }

    static MysqlSqlCompletionCandidateBuildResult apply(MysqlSqlCompletionCandidateContext context,
                                                        MysqlSqlCompletionRelationScope.Relation relation,
                                                        MysqlSqlCompletionCandidateBuildResult result) {
        if (result == null || result.status() != SqlCompletionStatusEnum.SUCCESS || result.candidates().isEmpty()) {
            return result;
        }
        MysqlSqlCompletionInsertColumnListContext insertContext = context.insertStatementContext().columnListContext();
        if (!insertContext.active()) {
            return result;
        }
        if (relation != null && !insertContext.matchesTable(relation)) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        List<SqlCompletionCandidate> filtered = filterInsertColumnCandidates(insertContext, result.candidates());
        if (StringUtils.isBlank(context.prefix()) && !filtered.isEmpty()) {
            filtered.add(allRemainingColumnsCandidate(filtered));
        }
        return MysqlSqlCompletionCandidateBuildResult.success(filtered);
    }

    private static List<SqlCompletionCandidate> filterInsertColumnCandidates(
            MysqlSqlCompletionInsertColumnListContext insertContext,
            List<SqlCompletionCandidate> candidates) {
        Map<String, SqlCompletionCandidate> unique = new LinkedHashMap<>();
        for (SqlCompletionCandidate candidate : candidates) {
            if (candidate == null || candidate.getType() != SqlCompletionCandidateTypeEnum.COLUMN) {
                continue;
            }
            String column = StringUtils.defaultIfBlank(candidate.getColumnName(), candidate.getLabel());
            String identity = MysqlSqlCompletionInsertColumnListContext.normalizeIdentifier(column);
            if (StringUtils.isBlank(identity) || insertContext.hasWrittenColumn(identity)) {
                continue;
            }
            unique.putIfAbsent(identity, candidate);
        }
        return new ArrayList<>(unique.values());
    }

    private static SqlCompletionCandidate allRemainingColumnsCandidate(List<SqlCompletionCandidate> columns) {
        String remainingColumns = String.join(", ", columns.stream()
                .map(MysqlSqlCompletionInsertColumnListCandidateFilter::candidateInsertText)
                .filter(StringUtils::isNotBlank)
                .toList());
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.COLUMN,
                remainingColumns);
        candidate.setInsertText(remainingColumns);
        candidate.setDetail("Insert all remaining columns");
        candidate.setSortRank(ALL_REMAINING_COLUMNS_SORT_RANK);
        return candidate;
    }

    private static String candidateInsertText(SqlCompletionCandidate candidate) {
        return StringUtils.defaultIfBlank(candidate.getInsertText(),
                StringUtils.defaultIfBlank(candidate.getColumnName(), candidate.getLabel()));
    }
}
