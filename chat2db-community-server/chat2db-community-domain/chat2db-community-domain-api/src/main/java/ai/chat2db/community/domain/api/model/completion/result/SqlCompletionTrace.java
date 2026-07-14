package ai.chat2db.community.domain.api.model.completion.result;

import java.util.List;

public record SqlCompletionTrace(List<SqlCompletionTraceStep> steps) {

    public SqlCompletionTrace {
        steps = steps == null ? List.of() : List.copyOf(steps);
    }

    public static SqlCompletionTrace empty() {
        return new SqlCompletionTrace(List.of());
    }
}
