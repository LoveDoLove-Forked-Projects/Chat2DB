package ai.chat2db.plugin.mysql.completion.provider.table;

import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.completion.provider.filter.MysqlSqlCompletionCompletedIdentifierFilter;
import ai.chat2db.plugin.mysql.completion.provider.metadata.MysqlSqlCompletionDatabaseQualifierCandidateDecorator;
import ai.chat2db.plugin.mysql.completion.provider.metadata.MysqlSqlCompletionMetadataCandidateQuery;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


final class MysqlSqlCompletionMetadataTableCandidateProvider {

    private MysqlSqlCompletionMetadataTableCandidateProvider() {
    }

    static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context) {
        return queryAll(context, List.of(
                SqlCompletionCandidateTypeEnum.TABLE_VIEW,
                SqlCompletionCandidateTypeEnum.DATABASE));
    }

    static MysqlSqlCompletionCandidateBuildResult buildDotScoped(MysqlSqlCompletionCandidateContext context) {
        if (qualifiedTableReferenceCompleted(context)) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return query(context, SqlCompletionCandidateTypeEnum.TABLE_VIEW, dotScopedSchemaScope(context),
                tableReferencePrefix(context));
    }

    static MysqlSqlCompletionCandidateBuildResult buildQualifiers(MysqlSqlCompletionCandidateContext context) {
        if (context == null) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return queryAll(context, List.of(SqlCompletionCandidateTypeEnum.DATABASE));
    }

    private static MysqlSqlCompletionCandidateBuildResult queryAll(MysqlSqlCompletionCandidateContext context,
                                                                   List<SqlCompletionCandidateTypeEnum> types) {
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        boolean unsupported = false;
        boolean supported = false;
        for (SqlCompletionCandidateTypeEnum type : types) {
            MysqlSqlCompletionCandidateBuildResult result = query(context, type);
            if (result.status() == SqlCompletionStatusEnum.UNSUPPORTED) {
                unsupported = true;
                continue;
            }
            supported = true;
            candidates.addAll(result.candidates());
        }
        if (candidates.isEmpty()) {
            return !supported && unsupported
                    ? MysqlSqlCompletionCandidateBuildResult.unsupported()
                    : MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static MysqlSqlCompletionCandidateBuildResult query(MysqlSqlCompletionCandidateContext context,
                                                                SqlCompletionCandidateTypeEnum type) {
        return query(context, type, SqlCompletionMetadataScope.empty(), tableReferencePrefix(context));
    }

    private static MysqlSqlCompletionCandidateBuildResult query(MysqlSqlCompletionCandidateContext context,
                                                                SqlCompletionCandidateTypeEnum type,
                                                                SqlCompletionMetadataScope scope,
                                                                String prefix) {
        MysqlSqlCompletionCandidateBuildResult result = MysqlSqlCompletionMetadataCandidateQuery.query(context, type,
                scope, prefix);
        if (result.status() != SqlCompletionStatusEnum.SUCCESS) {
            return result;
        }
        if (type == SqlCompletionCandidateTypeEnum.TABLE_VIEW
                && MysqlSqlCompletionCompletedIdentifierFilter.hasCompletedIdentifierMatch(context,
                result.candidates().stream()
                        .map(MysqlSqlCompletionMetadataTableCandidateProvider::candidateName)
                        .toList())) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(result.candidates().stream()
                .filter(candidate -> !MysqlSqlCompletionCompletedIdentifierFilter.repeatsCompletedIdentifier(
                        context, candidateName(candidate)))
                .map(candidate -> decorateDatabaseQualifierCandidate(type, candidate))
                .toList());
    }

    private static SqlCompletionCandidate decorateDatabaseQualifierCandidate(SqlCompletionCandidateTypeEnum type,
                                                                            SqlCompletionCandidate candidate) {
        if (type != SqlCompletionCandidateTypeEnum.DATABASE) {
            return candidate;
        }
        return MysqlSqlCompletionDatabaseQualifierCandidateDecorator.decorate(candidate);
    }

    private static String tableReferencePrefix(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.cursorContext() == null) {
            return "";
        }
        SqlCompletionCursorContext cursorContext = context.cursorContext();
        return cursorContext.dotScoped() ? cursorContext.prefix() : context.prefix();
    }

    private static boolean qualifiedTableReferenceCompleted(MysqlSqlCompletionCandidateContext context) {
        SqlCompletionMetadataScope scope = context.cursorContext().scope();
        return scope != null
                && StringUtils.isNotBlank(scope.schema())
                && StringUtils.isNotBlank(scope.table());
    }

    private static SqlCompletionMetadataScope dotScopedSchemaScope(MysqlSqlCompletionCandidateContext context) {
        String schema = context.cursorContext().scope().table();
        return new SqlCompletionMetadataScope(null, schema, null, null);
    }

    private static String candidateName(SqlCompletionCandidate candidate) {
        if (candidate == null) {
            return "";
        }
        return StringUtils.defaultIfBlank(candidate.getTableName(),
                StringUtils.defaultIfBlank(candidate.getObjectName(), candidate.getLabel()));
    }
}
