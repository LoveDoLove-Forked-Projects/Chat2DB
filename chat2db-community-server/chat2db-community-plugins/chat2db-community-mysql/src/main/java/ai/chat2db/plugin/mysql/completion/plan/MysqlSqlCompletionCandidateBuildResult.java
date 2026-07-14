package ai.chat2db.plugin.mysql.completion.plan;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;

public record MysqlSqlCompletionCandidateBuildResult(SqlCompletionStatusEnum status,
                                                     List<SqlCompletionCandidate> candidates) {

    public MysqlSqlCompletionCandidateBuildResult {
        status = status == null ? SqlCompletionStatusEnum.EMPTY : status;
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
    }

    public static MysqlSqlCompletionCandidateBuildResult empty() {
        return new MysqlSqlCompletionCandidateBuildResult(SqlCompletionStatusEnum.EMPTY, List.of());
    }

    public static MysqlSqlCompletionCandidateBuildResult success(List<SqlCompletionCandidate> candidates) {
        return new MysqlSqlCompletionCandidateBuildResult(SqlCompletionStatusEnum.SUCCESS, candidates);
    }

    public static MysqlSqlCompletionCandidateBuildResult unsupported() {
        return new MysqlSqlCompletionCandidateBuildResult(SqlCompletionStatusEnum.UNSUPPORTED, List.of());
    }
}
