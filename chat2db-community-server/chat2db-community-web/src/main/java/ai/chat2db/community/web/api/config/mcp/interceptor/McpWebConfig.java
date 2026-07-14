package ai.chat2db.community.web.api.config.mcp.interceptor;

import ai.chat2db.community.tools.annotation.NotCliRuntime;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@NotCliRuntime
public class McpWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new McpAuthInterceptor())
                .addPathPatterns("/mcp", "/mcp/**");
    }
}
