package ai.chat2db.community.web.api.adapter.db.execution;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SqlExecutionEvent {

    private String executionId;

    private Long eventSequence;

    private Long occurredAtEpochMs;

    private String eventType;

    private Integer statementSequence;

    private Integer resultSequence;

    private String resultKey;

    private Object message;
}
