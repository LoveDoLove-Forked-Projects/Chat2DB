package ai.chat2db.community.domain.core.impl.file;

import ai.chat2db.community.storage.imports.FileImportStagingStore;
import ai.chat2db.community.tools.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the two staging behaviours an end-to-end run broke: resolving a staged file whose state
 * directory is reached through a symlink, and reporting an over-sized upload as a size problem.
 */
class ImportFileStagingServicePathTest {

    private String previousUserHome;

    @BeforeEach
    void rememberUserHome() {
        previousUserHome = System.getProperty("user.home");
    }

    @AfterEach
    void restoreUserHome() {
        System.setProperty("user.home", previousUserHome);
    }

    @Test
    void resolvesAStagedFileWhenTheStateDirectoryIsBehindASymlink(@TempDir Path realHome) throws Exception {
        // macOS puts /tmp behind a symlink to /private/tmp; toRealPath() expands it, the staging
        // directory did not, so resolve() used to reject a perfectly readable file.
        Path link = realHome.getParent().resolve("state-link-" + System.nanoTime());
        Files.createSymbolicLink(link, realHome);
        System.setProperty("user.home", link.toString());
        ImportFileStagingService service = service();
        File source = Files.writeString(realHome.resolve("rows.csv"), "id\n1\n", StandardCharsets.UTF_8).toFile();

        String fileId = service.stage(source, "rows.csv");

        File resolved = service.resolve(fileId);

        assertTrue(resolved.isFile(), "a staged file behind a symlinked state directory must resolve");
        assertEquals("id\n1\n", Files.readString(resolved.toPath(), StandardCharsets.UTF_8));
    }

    @Test
    void rejectsAnOversizedUploadWithTheSizeMessage(@TempDir Path realHome) throws Exception {
        System.setProperty("user.home", realHome.toString());
        ImportFileStagingService service = service();
        File oversized = realHome.resolve("too-big.csv").toFile();
        try (RandomAccessFile file = new RandomAccessFile(oversized, "rw")) {
            file.setLength(51L * 1024 * 1024);
        }

        BusinessException failure = assertThrows(BusinessException.class,
                () -> service.stage(oversized, "too-big.csv"));

        assertEquals("import.preview.fileTooLarge", failure.getCode());
        assertEquals(50L, failure.getArgs()[0]);
    }

    /** The shipped locations, so the test covers the directory the product really writes to. */
    private ImportFileStagingService service() {
        FileImportStagingStore store = new FileImportStagingStore(new ObjectMapper(),
                FileImportStagingStore.defaultRootDirectory(), FileImportStagingStore.defaultLegacyDirectory());
        store.prepareRootDirectory();
        return new ImportFileStagingService(store, ImportFileStagingService.DEFAULT_MAX_TOTAL_BYTES);
    }
}
