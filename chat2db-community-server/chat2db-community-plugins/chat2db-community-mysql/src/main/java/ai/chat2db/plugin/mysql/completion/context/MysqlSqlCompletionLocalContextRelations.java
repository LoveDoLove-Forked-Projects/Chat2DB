package ai.chat2db.plugin.mysql.completion.context;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.scope.MysqlSqlCompletionRelationScope;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalRelation;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionLocalSymbolSourceTypeEnum;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MysqlSqlCompletionLocalContextRelations {

    private MysqlSqlCompletionLocalContextRelations() {
    }

    public static List<MysqlSqlCompletionRelationScope.Relation> relations(
            MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.localContext() == null) {
            return List.of();
        }
        List<SqlCompletionLocalRelation> localRelations = context.localContext().relations();
        return localRelations.stream()
                .map(MysqlSqlCompletionLocalContextRelations::relation)
                .toList();
    }

    public static List<MysqlSqlCompletionRelationScope.Relation> columnRelations(
            MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.localContext() == null) {
            return List.of();
        }
        List<SqlCompletionLocalRelation> localRelations = context.localContext().relations();
        Map<String, SqlCompletionLocalRelation> columnSources = columnSources(localRelations);
        return localRelations.stream()
                .filter(relation -> shouldPublish(relation, localRelations))
                .map(relation -> relation(relation, columnSource(columnSources, relation)))
                .toList();
    }

    private static Map<String, SqlCompletionLocalRelation> columnSources(List<SqlCompletionLocalRelation> relations) {
        Map<String, SqlCompletionLocalRelation> result = new LinkedHashMap<>();
        for (SqlCompletionLocalRelation relation : relations) {
            if (relation == null || relation.columns().isEmpty()) {
                continue;
            }
            result.putIfAbsent(relationKey(relation), relation);
        }
        return result;
    }

    private static SqlCompletionLocalRelation columnSource(Map<String, SqlCompletionLocalRelation> columnSources,
                                                           SqlCompletionLocalRelation relation) {
        SqlCompletionLocalRelation exact = columnSources.get(relationKey(relation));
        if (exact != null) {
            return exact;
        }
        return columnSources.values().stream()
                .filter(source -> canProvideColumns(source, relation))
                .findFirst()
                .orElse(null);
    }

    private static boolean shouldPublish(SqlCompletionLocalRelation relation,
                                         List<SqlCompletionLocalRelation> relations) {
        if (relation == null) {
            return false;
        }
        if (sourceType(relation) != SqlCompletionLocalSymbolSourceTypeEnum.DRAFT_DDL) {
            return true;
        }
        return relations.stream()
                .filter(source -> source != relation)
                .filter(source -> sourceType(source) != SqlCompletionLocalSymbolSourceTypeEnum.DRAFT_DDL)
                .noneMatch(source -> matchesTable(source, relation));
    }

    private static MysqlSqlCompletionRelationScope.Relation relation(SqlCompletionLocalRelation relation,
                                                                    SqlCompletionLocalRelation columnSource) {
        SqlCompletionLocalRelation resolvedColumnSource = columnSource == null ? relation : columnSource;
        return new MysqlSqlCompletionRelationScope.Relation(relation.catalog(), relation.schema(), relation.name(),
                relation.alias(), resolvedColumnSource.columns().stream()
                .map(column -> column.name())
                .toList(), true);
    }

    private static MysqlSqlCompletionRelationScope.Relation relation(SqlCompletionLocalRelation relation) {
        return new MysqlSqlCompletionRelationScope.Relation(relation.catalog(), relation.schema(), relation.name(),
                relation.alias(), relation.columns().stream()
                .map(column -> column.name())
                .toList(), true);
    }

    private static String relationKey(SqlCompletionLocalRelation relation) {
        return String.join("|", normalize(relation.catalog()), normalize(relation.schema()), normalize(relation.name()));
    }

    private static boolean matchesTable(SqlCompletionLocalRelation left, SqlCompletionLocalRelation right) {
        return canProvideColumns(left, right);
    }

    private static boolean canProvideColumns(SqlCompletionLocalRelation source, SqlCompletionLocalRelation target) {
        if (source == null || target == null || !Objects.equals(normalize(source.name()), normalize(target.name()))) {
            return false;
        }
        return compatibleQualifier(source.catalog(), target.catalog())
                && compatibleQualifier(source.schema(), target.schema());
    }

    private static boolean compatibleQualifier(String source, String target) {
        String normalizedSource = normalize(source);
        String normalizedTarget = normalize(target);
        return normalizedSource.isEmpty()
                || normalizedTarget.isEmpty()
                || Objects.equals(normalizedSource, normalizedTarget);
    }

    private static String normalize(String value) {
        return Objects.toString(value, "").trim().toLowerCase();
    }

    private static SqlCompletionLocalSymbolSourceTypeEnum sourceType(SqlCompletionLocalRelation relation) {
        return relation == null ? SqlCompletionLocalSymbolSourceTypeEnum.CURRENT_STATEMENT
                : SqlCompletionLocalSymbolSourceTypeEnum.from(relation.sourceType());
    }
}
