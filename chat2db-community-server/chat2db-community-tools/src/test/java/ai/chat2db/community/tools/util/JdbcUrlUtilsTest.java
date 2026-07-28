package ai.chat2db.community.tools.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression coverage for {@link JdbcUrlUtils#resetUrl(String, String, String)}:
 * {@code ~} home-directory expansion for H2/SQLite LocalFile URLs must happen on
 * every platform, not just Windows.
 */
class JdbcUrlUtilsTest {

    @Test
    void tildeIsExpandedForSQLiteLocalFile() {
        String result = JdbcUrlUtils.resetUrl("jdbc:sqlite:~/test.db", "SQLite", "LocalFile");
        assertFalse(result.contains("~"));
        assertTrue(result.startsWith("jdbc:sqlite:" + System.getProperty("user.home").replace("/", "\\"))
            || result.startsWith("jdbc:sqlite:" + System.getProperty("user.home")));
        assertTrue(result.endsWith("test.db"));
    }

    @Test
    void tildeIsExpandedForH2LocalFile() {
        String result = JdbcUrlUtils.resetUrl("jdbc:h2:~/data/app", "H2", "LocalFile");
        assertFalse(result.contains("~"));
        assertTrue(result.endsWith("data" + System.getProperty("file.separator") + "app")
            || result.endsWith("data/app") || result.endsWith("data\\app"));
    }

    @Test
    void nonLocalFileServiceTypeIsUntouched() {
        String url = "jdbc:sqlite:~/test.db";
        assertEquals(url, JdbcUrlUtils.resetUrl(url, "SQLite", "Remote"));
    }

    @Test
    void nonH2SqliteTypeIsUntouched() {
        String url = "jdbc:mysql://~/db";
        assertEquals(url, JdbcUrlUtils.resetUrl(url, "MySQL", "LocalFile"));
    }

    @Test
    void blankUrlIsUntouched() {
        assertEquals("", JdbcUrlUtils.resetUrl("", "SQLite", "LocalFile"));
    }
}
