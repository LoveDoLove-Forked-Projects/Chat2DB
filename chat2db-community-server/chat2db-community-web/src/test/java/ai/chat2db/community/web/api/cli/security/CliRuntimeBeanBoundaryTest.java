package ai.chat2db.community.web.api.util;

import ai.chat2db.community.tools.annotation.NotCliRuntime;
import ai.chat2db.community.web.api.config.cli.security.CliRuntimeOnly;
import ai.chat2db.community.web.api.controller.McpConfigController;
import ai.chat2db.community.web.api.config.mcp.Chat2dbMcpConfiguration;
import ai.chat2db.community.web.api.config.mcp.interceptor.McpWebConfig;
import ai.chat2db.community.web.api.config.mcp.security.DesktopMcpHttpFilter;
import ai.chat2db.community.web.api.mcp.adapter.AiToolMcpAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliRuntimeBeanBoundaryTest {

    @Test
    void cliRuntimeOnlyLoadsOnlyWhenRuntimeModeIsCli() {
        ConditionalOnProperty condition = CliRuntimeOnly.class.getAnnotation(ConditionalOnProperty.class);

        assertEquals(CliRuntimeUtils.RUNTIME_MODE_PROPERTY, condition.name()[0]);
        assertEquals("cli", condition.havingValue());
    }

    @Test
    void notCliRuntimeExcludesBeansWhenRuntimeModeIsCli() {
        ConditionalOnExpression condition = NotCliRuntime.class.getAnnotation(ConditionalOnExpression.class);

        assertEquals("!'${chat2db.runtime.mode:}'.equalsIgnoreCase('cli')", condition.value());
    }

    @Test
    void desktopAndMcpBeansAreExcludedFromCliRuntime() {
        assertNotCliRuntime(McpConfigController.class);
        assertNotCliRuntime(Chat2dbMcpConfiguration.class);
        assertNotCliRuntime(McpWebConfig.class);
        assertNotCliRuntime(DesktopMcpHttpFilter.class);
        assertNotCliRuntime(AiToolMcpAdapter.class);
    }

    private static void assertNotCliRuntime(Class<?> type) {
        assertTrue(type.isAnnotationPresent(NotCliRuntime.class), type.getName());
    }
}
