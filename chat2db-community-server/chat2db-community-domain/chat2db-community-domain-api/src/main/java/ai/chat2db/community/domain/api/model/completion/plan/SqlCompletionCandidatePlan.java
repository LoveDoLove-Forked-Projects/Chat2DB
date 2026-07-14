package ai.chat2db.community.domain.api.model.completion.plan;

import java.util.List;

public record SqlCompletionCandidatePlan(List<SqlCompletionCandidatePlanItem> items) {

    public SqlCompletionCandidatePlan {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public static SqlCompletionCandidatePlan empty() {
        return new SqlCompletionCandidatePlan(List.of());
    }
}
