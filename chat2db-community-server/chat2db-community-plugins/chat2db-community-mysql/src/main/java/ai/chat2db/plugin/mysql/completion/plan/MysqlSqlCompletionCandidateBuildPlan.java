package ai.chat2db.plugin.mysql.completion.plan;

import ai.chat2db.community.domain.api.enums.completion.SqlCompletionStatusEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.ArrayList;
import java.util.List;


public record MysqlSqlCompletionCandidateBuildPlan(MysqlSqlCompletionCandidateBuildResult primary,
                                                   MysqlSqlCompletionCandidateBuildResult secondary) {

    public MysqlSqlCompletionCandidateBuildPlan {
        primary = primary == null ? MysqlSqlCompletionCandidateBuildResult.empty() : primary;
        secondary = secondary == null ? MysqlSqlCompletionCandidateBuildResult.empty() : secondary;
    }

    public static MysqlSqlCompletionCandidateBuildPlan empty() {
        return primary(MysqlSqlCompletionCandidateBuildResult.empty());
    }

    public static MysqlSqlCompletionCandidateBuildPlan primary(MysqlSqlCompletionCandidateBuildResult primary) {
        return new MysqlSqlCompletionCandidateBuildPlan(primary, MysqlSqlCompletionCandidateBuildResult.empty());
    }

    public boolean unsupported() {
        return primary.status() == SqlCompletionStatusEnum.UNSUPPORTED
                || secondary.status() == SqlCompletionStatusEnum.UNSUPPORTED;
    }

    public boolean hasNoCandidates() {
        return primary.candidates().isEmpty() && secondary.candidates().isEmpty();
    }

    public boolean hasPrimaryCandidates() {
        return !primary.candidates().isEmpty();
    }

    public List<SqlCompletionCandidate> candidates() {
        List<SqlCompletionCandidate> candidates = new ArrayList<>();
        candidates.addAll(secondary.candidates());
        candidates.addAll(primary.candidates());
        return candidates;
    }
}
