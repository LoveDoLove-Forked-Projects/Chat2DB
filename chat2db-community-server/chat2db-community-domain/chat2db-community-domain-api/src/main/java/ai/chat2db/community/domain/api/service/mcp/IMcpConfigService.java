package ai.chat2db.community.domain.api.service.mcp;

/**
 * Builds MCP integration configuration content.
 */
public interface IMcpConfigService {

    /**
     * Returns MCP configuration JSON text for local integrations.
     *
     * @return MCP configuration JSON text.
     */
    String copyConfig();
}
