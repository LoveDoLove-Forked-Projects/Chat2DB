package ai.chat2db.community.tools.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Smoke coverage for {@link JdbcJarUtils#asyncDownload(String)}: connection
 * failures are handled asynchronously by the OkHttp callback (and logged via
 * {@code log.warn}), so the call itself must return without throwing even when
 * the target is unreachable.
 */
class JdbcJarUtilsTest {

    @Test
    void asyncDownloadOfUnreachableUrlDoesNotThrowSynchronously() {
        assertDoesNotThrow(() -> JdbcJarUtils.asyncDownload("http://localhost:1/nonexistent-driver.jar"));
    }
}
