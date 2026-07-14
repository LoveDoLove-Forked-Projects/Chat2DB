package ai.chat2db.plugin.mysql.completion.provider.table;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.List;


public final class MysqlSqlCompletionTableCandidateProvider {

    private MysqlSqlCompletionTableCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context) {
        if (dotScopedTableReference(context)) {
            return MysqlSqlCompletionMetadataTableCandidateProvider.buildDotScoped(context);
        }
        MysqlSqlCompletionCandidateBuildResult localResult = MysqlSqlCompletionLocalTableCandidateProvider.build(context);
        MysqlSqlCompletionCandidateBuildResult metadataResult = MysqlSqlCompletionMetadataTableCandidateProvider
                .build(context);
        if (localResult.candidates().isEmpty()) {
            return metadataResult;
        }
        if (metadataResult.status() == SqlCompletionStatusEnum.UNSUPPORTED || metadataResult.candidates().isEmpty()) {
            return localResult;
        }
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        candidates.addAll(localResult.candidates());
        candidates.addAll(metadataResult.candidates());
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    public static MysqlSqlCompletionCandidateBuildResult buildQualifiers(MysqlSqlCompletionCandidateContext context) {
        return MysqlSqlCompletionMetadataTableCandidateProvider.buildQualifiers(context);
    }

    private static boolean dotScopedTableReference(MysqlSqlCompletionCandidateContext context) {
        return context != null
                && context.cursorContext() != null
                && context.cursorContext().dotScoped();
    }
}
