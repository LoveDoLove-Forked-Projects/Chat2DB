package ai.chat2db.plugin.mysql.completion.provider.column;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.completion.analysis.statement.ddl.trigger.MysqlCreateTriggerStatementPseudoRecordAnalyzer;
import ai.chat2db.plugin.mysql.completion.provider.filter.MysqlSqlCompletionCompletedIdentifierFilter;
import ai.chat2db.plugin.mysql.completion.provider.metadata.MysqlSqlCompletionMetadataCandidateQuery;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


final class MysqlSqlCompletionColumnCandidateProvider {

    private MysqlSqlCompletionColumnCandidateProvider() {
    }

    static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context,
                                                        MysqlSqlCompletionRelationScope relationScope) {
        if (context.cursorContext().dotScoped()) {
            return scopedColumns(context, relationScope);
        }
        return unqualifiedColumns(context, relationScope);
    }

    private static MysqlSqlCompletionCandidateBuildResult scopedColumns(MysqlSqlCompletionCandidateContext context,
                                                                        MysqlSqlCompletionRelationScope relationScope) {
        return MysqlCreateTriggerStatementPseudoRecordAnalyzer.resolvePseudoRecordTargetRelationScope(context)
                .map(scope -> scopedColumnsForRelations(context, scope.relations()))
                .orElseGet(() -> scopedColumns(context, relationScope, true));
    }

    private static MysqlSqlCompletionCandidateBuildResult scopedColumnsForRelations(
            MysqlSqlCompletionCandidateContext context,
            List<MysqlSqlCompletionRelationScope.Relation> relations) {
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        boolean unsupported = false;
        for (MysqlSqlCompletionRelationScope.Relation relation : relations) {
            MysqlSqlCompletionCandidateBuildResult result = columns(context, relation, false);
            unsupported = unsupported || result.status() == SqlCompletionStatusEnum.UNSUPPORTED;
            candidates.addAll(result.candidates());
        }
        if (candidates.isEmpty() && unsupported) {
            return MysqlSqlCompletionCandidateBuildResult.unsupported();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static MysqlSqlCompletionCandidateBuildResult scopedColumns(MysqlSqlCompletionCandidateContext context,
                                                                        MysqlSqlCompletionRelationScope relationScope,
                                                                        boolean fallbackToOwnerTable) {
        SqlCompletionMetadataScope cursorScope = context.cursorContext().scope();
        if (StringUtils.isNotBlank(cursorScope.schema()) && StringUtils.isNotBlank(cursorScope.table())) {
            return columns(context, cursorScope);
        }
        String owner = context.cursorContext().scope().table();
        List<MysqlSqlCompletionRelationScope.Relation> relations = relationScope.resolveOwner(owner);
        if (relations.isEmpty() && fallbackToOwnerTable && relationScope.relations().isEmpty()
                && StringUtils.isNotBlank(owner)) {
            return columns(context, new MysqlSqlCompletionRelationScope.Relation(cursorScope.catalog(),
                    cursorScope.schema(), owner, null), false);
        }
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        boolean unsupported = false;
        for (MysqlSqlCompletionRelationScope.Relation relation : relations) {
            MysqlSqlCompletionCandidateBuildResult result = columns(context, relation, false);
            unsupported = unsupported || result.status() == SqlCompletionStatusEnum.UNSUPPORTED;
            candidates.addAll(result.candidates());
        }
        if (candidates.isEmpty() && unsupported) {
            return MysqlSqlCompletionCandidateBuildResult.unsupported();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static MysqlSqlCompletionCandidateBuildResult unqualifiedColumns(MysqlSqlCompletionCandidateContext context,
                                                                             MysqlSqlCompletionRelationScope relationScope) {
        if (relationScope.relations().isEmpty()) {
            if (context.insertStatementContext().columnListContext().active()) {
                return MysqlSqlCompletionCandidateBuildResult.empty();
            }
            return genericColumns(context);
        }
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        boolean unsupported = false;
        for (MysqlSqlCompletionRelationScope.Relation relation : relationScope.relations()) {
            MysqlSqlCompletionCandidateBuildResult result = columns(context, relation, true);
            unsupported = unsupported || result.status() == SqlCompletionStatusEnum.UNSUPPORTED;
            candidates.addAll(MysqlSqlCompletionInsertColumnListCandidateFilter.apply(context, relation, result)
                    .candidates());
        }
        if (candidates.isEmpty() && unsupported) {
            return MysqlSqlCompletionCandidateBuildResult.unsupported();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static MysqlSqlCompletionCandidateBuildResult columns(MysqlSqlCompletionCandidateContext context,
                                                                 MysqlSqlCompletionRelationScope.Relation relation,
                                                                 boolean qualifyInsertText) {
        if (relation == null || StringUtils.isBlank(relation.table())) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        if (relation.hasLocalColumns()) {
            return MysqlSqlCompletionCandidateBuildResult.success(relation.columns().stream()
                    .filter(column -> StringUtils.startsWithIgnoreCase(column, context.prefix()))
                    .filter(column -> !MysqlSqlCompletionCompletedIdentifierFilter.repeatsCompletedIdentifier(
                            context, column))
                    .map(column -> localColumn(column, relation.table(), relation.alias()))
                    .map(candidate -> MysqlSqlCompletionRelationColumnCandidateDecorator.decorate(candidate, relation,
                            qualifyInsertText))
                    .toList());
        }
        SqlCompletionMetadataScope scope = new SqlCompletionMetadataScope(
                relation.catalog(), relation.schema(), relation.table(), null);
        MysqlSqlCompletionCandidateBuildResult result = columns(context, scope);
        if (result.status() != SqlCompletionStatusEnum.SUCCESS || result.candidates().isEmpty()) {
            return result;
        }
        MysqlSqlCompletionRelationColumnCandidateDecorator.decorate(result.candidates(), relation, qualifyInsertText);
        return result;
    }

    private static SqlCompletionCandidate localColumn(String column, String table, String alias) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.COLUMN, column);
        candidate.setTableName(table);
        candidate.setTableAlias(alias);
        candidate.setColumnName(column);
        candidate.setSortRank(150);
        return candidate;
    }

    private static MysqlSqlCompletionCandidateBuildResult genericColumns(MysqlSqlCompletionCandidateContext context) {
        return columns(context, SqlCompletionMetadataScope.empty());
    }

    private static MysqlSqlCompletionCandidateBuildResult columns(MysqlSqlCompletionCandidateContext context,
                                                                 SqlCompletionMetadataScope scope) {
        MysqlSqlCompletionCandidateBuildResult result = MysqlSqlCompletionMetadataCandidateQuery.query(context,
                SqlCompletionCandidateTypeEnum.COLUMN, scope, context.prefix());
        if (result.status() != SqlCompletionStatusEnum.SUCCESS) {
            return result;
        }
        return MysqlSqlCompletionCandidateBuildResult.success(result.candidates().stream()
                .filter(candidate -> !MysqlSqlCompletionCompletedIdentifierFilter.repeatsCompletedIdentifier(
                        context, candidateName(candidate)))
                .toList());
    }

    private static String candidateName(SqlCompletionCandidate candidate) {
        if (candidate == null) {
            return "";
        }
        return StringUtils.defaultIfBlank(candidate.getColumnName(), candidate.getLabel());
    }
}
