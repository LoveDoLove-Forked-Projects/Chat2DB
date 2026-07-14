package ai.chat2db.community.web.api.adapter.db.execution;

import lombok.Data;

@Data
public class SqlExecutionCancelRequest {

    private String executionId;
}
