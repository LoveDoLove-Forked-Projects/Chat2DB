package ai.chat2db.community.tools.console.bridge;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SqlExecuteResponse {

    private Object startResult;

    private String requestUuid;

    private String executionId;
}
