package ai.chat2db.plugin.mysql.completion.provider.object;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.completion.provider.callable.MysqlSqlCompletionCallableCandidateSupport;
import ai.chat2db.plugin.mysql.completion.provider.metadata.MysqlSqlCompletionDatabaseQualifierCandidateDecorator;
import ai.chat2db.plugin.mysql.completion.provider.metadata.MysqlSqlCompletionMetadataCandidateQuery;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;
import java.util.ArrayList;
import java.util.List;


public final class MysqlSqlCompletionObjectCandidateProvider {

    private MysqlSqlCompletionObjectCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context,
                                                               SqlCompletionCandidateTypeEnum type) {
        if (type == null) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return merge(databaseQualifierCandidates(context, type), objectCandidates(context, type));
    }

    public static MysqlSqlCompletionCandidateBuildResult buildCallable(MysqlSqlCompletionCandidateContext context,
                                                                       SqlCompletionCandidateTypeEnum type) {
        MysqlSqlCompletionCandidateBuildResult result = objectCandidates(context, type);
        if (result.status() != SqlCompletionStatusEnum.SUCCESS || result.candidates().isEmpty()) {
            return databaseQualifierCandidates(context, type);
        }
        return merge(databaseQualifierCandidates(context, type), MysqlSqlCompletionCandidateBuildResult.success(
                MysqlSqlCompletionCallableCandidateSupport.enrich(context, type, result.candidates())));
    }

    private static MysqlSqlCompletionCandidateBuildResult objectCandidates(MysqlSqlCompletionCandidateContext context,
                                                                          SqlCompletionCandidateTypeEnum type) {
        MysqlSqlCompletionCandidateBuildResult result = MysqlSqlCompletionMetadataCandidateQuery.query(context, type,
                SqlCompletionMetadataScope.empty(), context.prefix());
        if (result.status() == SqlCompletionStatusEnum.UNSUPPORTED) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return result;
    }

    private static MysqlSqlCompletionCandidateBuildResult databaseQualifierCandidates(
            MysqlSqlCompletionCandidateContext context,
            SqlCompletionCandidateTypeEnum type) {
        if (!supportsDatabaseQualifier(type) || context == null || context.cursorContext() == null
                || context.cursorContext().dotScoped()) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        MysqlSqlCompletionCandidateBuildResult result = MysqlSqlCompletionMetadataCandidateQuery.query(context,
                SqlCompletionCandidateTypeEnum.DATABASE, SqlCompletionMetadataScope.empty(), context.prefix());
        if (result.status() == SqlCompletionStatusEnum.UNSUPPORTED) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(
                MysqlSqlCompletionDatabaseQualifierCandidateDecorator.decorate(result.candidates()));
    }

    private static boolean supportsDatabaseQualifier(SqlCompletionCandidateTypeEnum type) {
        return type == SqlCompletionCandidateTypeEnum.PROCEDURE
                || type == SqlCompletionCandidateTypeEnum.FUNCTION
                || type == SqlCompletionCandidateTypeEnum.EVENT
                || type == SqlCompletionCandidateTypeEnum.TRIGGER;
    }

    private static MysqlSqlCompletionCandidateBuildResult merge(MysqlSqlCompletionCandidateBuildResult left,
                                                                MysqlSqlCompletionCandidateBuildResult right) {
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        if (left != null) {
            candidates.addAll(left.candidates());
        }
        if (right != null) {
            candidates.addAll(right.candidates());
        }
        if (candidates.isEmpty()) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

}
