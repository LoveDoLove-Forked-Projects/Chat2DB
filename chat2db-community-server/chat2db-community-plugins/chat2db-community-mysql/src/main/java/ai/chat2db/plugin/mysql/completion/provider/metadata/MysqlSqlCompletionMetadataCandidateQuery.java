package ai.chat2db.plugin.mysql.completion.provider.metadata;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionMetadataScope;


public final class MysqlSqlCompletionMetadataCandidateQuery {

    private MysqlSqlCompletionMetadataCandidateQuery() {
    }

    public static MysqlSqlCompletionCandidateBuildResult query(MysqlSqlCompletionCandidateContext context,
                                                               SqlCompletionCandidateTypeEnum type,
                                                               SqlCompletionMetadataScope scope,
                                                               String prefix) {
        if (context == null || context.metadataProvider() == null || type == null) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        SqlCompletionMetadataResponse result = context.metadataProvider()
                .list(DbSqlCompletionMetadataRequest.of(type, scope, prefix));
        if (result == null) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        if (SqlCompletionStatusEnum.UNSUPPORTED.name().equals(result.getStatus())) {
            return MysqlSqlCompletionCandidateBuildResult.unsupported();
        }
        if (!SqlCompletionStatusEnum.SUCCESS.name().equals(result.getStatus())) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        return MysqlSqlCompletionCandidateBuildResult.success(result.getCandidates());
    }
}
