package ai.chat2db.community.domain.api.model.completion.result;

import java.util.Map;
import java.util.Objects;

public record SqlCompletionTraceStep(String stage,
                                     Map<String, Object> values) {

    public SqlCompletionTraceStep {
        stage = Objects.toString(stage, "");
        values = values == null ? Map.of() : Map.copyOf(values);
    }
}
