package ai.chat2db.community.tools.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CommunityEncryptionKeyStoreTest {

    private static final String EXPLICIT_KEY = encodedKey(7);

    @TempDir
    Path temporaryDirectory;

    private Path keyFile;

    @BeforeEach
    void setUp() {
        keyFile = temporaryDirectory.resolve("config/encryption.key");
        System.clearProperty(AesGcmUtil.KEY_PROPERTY);
        System.setProperty(AesGcmUtil.KEY_FILE_PROPERTY, keyFile.toString());
        System.setProperty("chat2db.runtime.mode", "community");
        System.setProperty("chat2db.mode", "DESKTOP");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty(AesGcmUtil.KEY_PROPERTY);
        System.clearProperty(AesGcmUtil.KEY_FILE_PROPERTY);
        System.clearProperty("chat2db.runtime.mode");
        System.clearProperty("chat2db.mode");
    }

    @Test
    void createsAndReusesDesktopKeyFile() throws Exception {
        String created = CommunityEncryptionKeyStore.resolve();
        String reused = CommunityEncryptionKeyStore.resolve();

        assertEquals(created, reused);
        assertEquals(32, Base64.getDecoder().decode(created).length);
        assertEquals(created, Files.readString(keyFile, StandardCharsets.UTF_8).trim());
        assertOwnerOnlyPermissionsWhenSupported(keyFile);
    }

    @Test
    void concurrentDesktopInitializationUsesOneKey() throws Exception {
        List<String> resolvedKeys = IntStream.range(0, 16)
                .parallel()
                .mapToObj(index -> CommunityEncryptionKeyStore.resolve())
                .toList();

        assertEquals(1, resolvedKeys.stream().distinct().count());
        assertEquals(resolvedKeys.get(0), Files.readString(keyFile, StandardCharsets.UTF_8).trim());
        assertOwnerOnlyPermissionsWhenSupported(keyFile);
    }

    @Test
    void explicitKeyTakesPrecedenceWithoutCreatingFile() {
        System.setProperty(AesGcmUtil.KEY_PROPERTY, EXPLICIT_KEY);

        assertEquals(EXPLICIT_KEY, CommunityEncryptionKeyStore.resolve());
        assertFalse(Files.exists(keyFile));
    }

    @Test
    void headlessRuntimeRequiresExplicitInitialization() {
        System.setProperty("chat2db.mode", "WEB");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                CommunityEncryptionKeyStore::resolve);

        assertTrue(exception.getMessage().contains(AesGcmUtil.KEY_PROPERTY));
        assertTrue(exception.getMessage().contains(keyFile.toString()));
        assertFalse(Files.exists(keyFile));
    }

    @Test
    void headlessRuntimeUsesExistingKeyFile() throws Exception {
        System.setProperty("chat2db.mode", "WEB");
        Files.createDirectories(keyFile.getParent());
        Files.writeString(keyFile, EXPLICIT_KEY, StandardCharsets.UTF_8);

        assertEquals(EXPLICIT_KEY, CommunityEncryptionKeyStore.resolve());
    }

    @Test
    void readsExistingReadOnlyKeyWithoutChangingPermissions() throws Exception {
        Files.createDirectories(keyFile.getParent());
        Files.writeString(keyFile, EXPLICIT_KEY, StandardCharsets.UTF_8);
        if (supportsPosixPermissions(keyFile)) {
            Files.setPosixFilePermissions(keyFile, Set.of(PosixFilePermission.OWNER_READ));
        }

        assertEquals(EXPLICIT_KEY, CommunityEncryptionKeyStore.resolve());
        if (supportsPosixPermissions(keyFile)) {
            assertEquals(Set.of(PosixFilePermission.OWNER_READ), Files.getPosixFilePermissions(keyFile));
        }
    }

    @Test
    void doesNotChangeExistingCustomDirectoryPermissions() throws Exception {
        Path keyDirectory = keyFile.getParent();
        Files.createDirectories(keyDirectory);
        Set<PosixFilePermission> expected = EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_EXECUTE);
        if (supportsPosixPermissions(keyDirectory)) {
            Files.setPosixFilePermissions(keyDirectory, expected);
        }

        String key = CommunityEncryptionKeyStore.resolve();

        assertEquals(32, Base64.getDecoder().decode(key).length);
        if (supportsPosixPermissions(keyDirectory)) {
            assertEquals(expected, Files.getPosixFilePermissions(keyDirectory));
        }
    }

    @Test
    void rejectsExplicitBlankConfigurationWithoutFallback() {
        System.setProperty(AesGcmUtil.KEY_PROPERTY, " ");

        assertThrows(IllegalStateException.class,
                () -> new AesGcmUtil(CommunityEncryptionKeyStore.resolve()));
        assertFalse(Files.exists(keyFile));
    }

    @Test
    void rejectsBlankConfiguredKeyFilePath() {
        System.setProperty(AesGcmUtil.KEY_FILE_PROPERTY, " ");

        assertThrows(IllegalStateException.class, CommunityEncryptionKeyStore::resolve);
    }

    @Test
    void invalidExistingKeyIsRejectedWithoutBeingOverwritten() throws Exception {
        Files.createDirectories(keyFile.getParent());
        Files.writeString(keyFile, "not-a-valid-key", StandardCharsets.UTF_8);

        assertThrows(IllegalStateException.class,
                () -> new AesGcmUtil(CommunityEncryptionKeyStore.resolve()));
        assertEquals("not-a-valid-key", Files.readString(keyFile, StandardCharsets.UTF_8));
    }

    private static void assertOwnerOnlyPermissionsWhenSupported(Path file) throws Exception {
        if (!supportsPosixPermissions(file)) {
            return;
        }
        Set<PosixFilePermission> expected = EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE);
        assertEquals(expected, Files.getPosixFilePermissions(file));
    }

    private static boolean supportsPosixPermissions(Path path) {
        return Files.getFileAttributeView(path, java.nio.file.attribute.PosixFileAttributeView.class) != null;
    }

    private static String encodedKey(int value) {
        byte[] bytes = new byte[32];
        Arrays.fill(bytes, (byte) value);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
