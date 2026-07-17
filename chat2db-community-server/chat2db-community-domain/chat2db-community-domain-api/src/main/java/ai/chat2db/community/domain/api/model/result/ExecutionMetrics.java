package ai.chat2db.community.domain.api.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionMetrics {

    private Long startedAtEpochMs;

    private Long finishedAtEpochMs;

    private Long executeDurationMs;

    private Long fetchDurationMs;

    private Integer fetchedRowCount;
}
