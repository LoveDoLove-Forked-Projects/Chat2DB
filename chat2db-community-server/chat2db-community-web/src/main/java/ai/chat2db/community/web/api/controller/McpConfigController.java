package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.service.mcp.IMcpConfigService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.tools.annotation.NotCliRuntime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes MCP configuration endpoints for local integrations.
 */
@RestController
@RequestMapping("/api/mcp")
@NotCliRuntime
public class McpConfigController {

    private final IMcpConfigService mcpConfigService;

    public McpConfigController(IMcpConfigService mcpConfigService) {
        this.mcpConfigService = mcpConfigService;
    }

    /**
     * Returns MCP configuration text for local integrations.
     * <p>
     * Endpoint: {@code GET /api/mcp/config/copy}.
     *
     * @return data result containing string.
     */
    @GetMapping("/config/copy")
    public DataResult<String> copyConfig() {
        return DataResult.of(mcpConfigService.copyConfig());
    }

}
