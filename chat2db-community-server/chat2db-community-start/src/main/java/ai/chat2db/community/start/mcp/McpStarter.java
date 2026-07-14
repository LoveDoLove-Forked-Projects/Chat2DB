package ai.chat2db.community.start.mcp;

import io.modelcontextprotocol.server.McpStatelessSyncServer;
import io.modelcontextprotocol.server.McpSyncServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "spring.ai.mcp.server.enabled", havingValue = "true")
@Component
@Slf4j
public class McpStarter {

    private final ObjectProvider<McpSyncServer> mcpSyncServerProvider;
    private final ObjectProvider<McpStatelessSyncServer> mcpStatelessSyncServerProvider;

    public McpStarter(ObjectProvider<McpSyncServer> mcpSyncServerProvider,
                      ObjectProvider<McpStatelessSyncServer> mcpStatelessSyncServerProvider) {
        this.mcpSyncServerProvider = mcpSyncServerProvider;
        this.mcpStatelessSyncServerProvider = mcpStatelessSyncServerProvider;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        log.info("Starting MCP Server");
        McpSyncServer syncServer = mcpSyncServerProvider.getIfAvailable();
        if (syncServer != null) {
            log.info("MCP sync server initialized");
            return;
        }
        McpStatelessSyncServer statelessSyncServer = mcpStatelessSyncServerProvider.getIfAvailable();
        if (statelessSyncServer != null) {
            log.info("MCP stateless sync server initialized");
            return;
        }
        log.warn("MCP server enabled, but no compatible MCP server bean was initialized");
    }


}
