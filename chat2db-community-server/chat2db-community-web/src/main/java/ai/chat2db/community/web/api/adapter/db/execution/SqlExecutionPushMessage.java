package ai.chat2db.community.web.api.adapter.db.execution;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SqlExecutionPushMessage {

    private String uuid;

    private String actionType;

    private SqlExecutionEvent message;
}
