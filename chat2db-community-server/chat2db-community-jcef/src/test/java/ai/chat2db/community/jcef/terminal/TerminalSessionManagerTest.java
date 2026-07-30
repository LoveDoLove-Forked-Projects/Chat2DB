package ai.chat2db.community.jcef.terminal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalSessionManagerTest {
    @TempDir
    Path directory;

    @Test
    void createsResizableInteractiveSessionInRequestedDirectory() throws Exception {
        Map<String, Object> session = TerminalSessionManager.create(directory, 80, 24);
        String sessionId = (String) session.get("sessionId");
        try {
            assertNotNull(sessionId);
            assertFalse(((String) session.get("shell")).isBlank());
            assertEquals("system", session.get("shellId"));
            assertTrue((Boolean) TerminalSessionManager.status(sessionId).get("alive"));
            TerminalSessionManager.resize(sessionId, 100, 30);
            TerminalSessionManager.write(sessionId, "echo chat2db-terminal-test\n");
        } finally {
            TerminalSessionManager.kill(sessionId);
        }
        assertFalse((Boolean) TerminalSessionManager.status(sessionId).get("alive"));
    }

    @Test
    void reportsRunningChildProcessAsBusy() throws Exception {
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            return;
        }
        Map<String, Object> session = TerminalSessionManager.create(directory, 80, 24);
        String sessionId = (String) session.get("sessionId");
        try {
            TerminalSessionManager.write(sessionId, "sleep 10\n");
            boolean busy = false;
            for (int attempt = 0; attempt < 40 && !busy; attempt++) {
                Thread.sleep(50);
                busy = (Boolean) TerminalSessionManager.status(sessionId).get("busy");
            }
            assertTrue(busy);
        } finally {
            TerminalSessionManager.kill(sessionId);
        }
    }

    @Test
    void duplicatesIntoIndependentSessionWithSameWorkingDirectory() throws Exception {
        Map<String, Object> original = TerminalSessionManager.create(directory, 80, 24);
        String originalSessionId = (String) original.get("sessionId");
        Map<String, Object> duplicate = TerminalSessionManager.duplicate(originalSessionId, 100, 30);
        String duplicateSessionId = (String) duplicate.get("sessionId");
        try {
            assertNotEquals(originalSessionId, duplicateSessionId);
            assertEquals(original.get("cwd"), duplicate.get("cwd"));
            assertEquals(original.get("shellId"), duplicate.get("shellId"));
            TerminalSessionManager.kill(originalSessionId);
            assertFalse((Boolean) TerminalSessionManager.status(originalSessionId).get("alive"));
            assertTrue((Boolean) TerminalSessionManager.status(duplicateSessionId).get("alive"));
        } finally {
            TerminalSessionManager.kill(originalSessionId);
            TerminalSessionManager.kill(duplicateSessionId);
        }
    }

    @Test
    void resolvesConfiguredUserHomeForDefaultTerminalDirectory() {
        assertEquals(directory, TerminalSessionManager.resolveUserHomeDirectory(directory.toString()));
    }

    @Test
    void exposesSystemDefaultAndInstalledShells() {
        Map<String, Object> capabilities = TerminalSessionManager.capabilities();
        assertNotNull(capabilities.get("os"));
        assertTrue(((java.util.List<?>) capabilities.get("shells")).stream()
                .map(option -> (Map<?, ?>) option)
                .anyMatch(option -> "system".equals(option.get("id")) && Boolean.TRUE.equals(option.get("available"))));
    }

    @Test
    void terminalThemeOverridesNoColorWithoutLeavingAConflict() {
        Map<String, String> environment = new HashMap<>();
        environment.put("NO_COLOR", "");

        assertTrue(TerminalSessionManager.applyColorEnvironment(environment));
        assertFalse(environment.containsKey("NO_COLOR"));
        assertEquals("1", environment.get("FORCE_COLOR"));
        assertEquals("1", environment.get("CLICOLOR_FORCE"));
        assertEquals("1", environment.get("CLICOLOR"));
    }

    @Test
    void enablesCompatibleColorHints() {
        Map<String, String> environment = new HashMap<>();

        assertTrue(TerminalSessionManager.applyColorEnvironment(environment));
        assertEquals("1", environment.get("CLICOLOR"));
        assertNotNull(environment.get("LSCOLORS"));
        assertNotNull(environment.get("LS_COLORS"));
        assertEquals("1", environment.get("FORCE_COLOR"));
        assertEquals("1", environment.get("CLICOLOR_FORCE"));
    }
}
