package ai.chat2db.community.web.api.model.request.mcp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class McpBaseRequest {

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;
}
