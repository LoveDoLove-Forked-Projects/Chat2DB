package ai.chat2db.community.web.api.mcp.context;

import ai.chat2db.community.web.api.model.request.mcp.McpBaseRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class McpContext {

    public static McpBaseRequest getBaseRequest() {
        long datasourceId = Long.parseLong(System.getenv("datasourceId"));
        String databaseName = System.getenv("databaseName");
        String schemaName = System.getenv("schemaName");
        return McpBaseRequest.builder().dataSourceId(datasourceId).databaseName(databaseName).schemaName(schemaName).build();
    }


}
