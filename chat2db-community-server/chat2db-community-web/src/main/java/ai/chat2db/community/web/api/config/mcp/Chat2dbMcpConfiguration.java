package ai.chat2db.community.web.api.config.mcp;

import ai.chat2db.community.tools.annotation.NotCliRuntime;
import ai.chat2db.community.web.api.mcp.adapter.AiToolMcpAdapter;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NotCliRuntime
public class Chat2dbMcpConfiguration {

    @Bean
    public ToolCallbackProvider register(AiToolMcpAdapter mcpServices) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpServices)
                .build();
    }
}
