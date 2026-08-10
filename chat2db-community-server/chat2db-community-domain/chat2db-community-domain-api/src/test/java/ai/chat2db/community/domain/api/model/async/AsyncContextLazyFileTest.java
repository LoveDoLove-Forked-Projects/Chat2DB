package ai.chat2db.community.domain.api.model.async;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncContextLazyFileTest {

    @TempDir
    Path tempDir;

    @Test
    void outputFileIsCreatedOnlyWhenWorkerWrites() throws Exception {
        Path output = tempDir.resolve("nested").resolve("export.sql");
        AsyncContext context = new AsyncContext(null, null, output.toFile(), true);

        assertFalse(Files.exists(output));

        context.write("select 1;");
        context.finish();

        assertTrue(Files.exists(output));
        assertEquals("select 1;\n", Files.readString(output));
    }

    @Test
    void finishingRejectedTaskDoesNotCreateOutputFile() {
        Path output = tempDir.resolve("rejected.sql");
        AsyncContext context = new AsyncContext(null, null, output.toFile(), true);

        context.error("rejected");
        context.finish();

        assertFalse(Files.exists(output));
    }
}
