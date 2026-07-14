package ai.chat2db.community.tools.security;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;

final class CommunityEncryptionKeyStore {

    private static final int KEY_LENGTH_BYTES = 32;
    private static final String COMMUNITY_RUNTIME_MODE = "community";
    private static final String DESKTOP_MODE = "DESKTOP";
    private static final Set<PosixFilePermission> DIRECTORY_PERMISSIONS = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE);
    private static final Set<PosixFilePermission> FILE_PERMISSIONS = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CommunityEncryptionKeyStore() {
    }

    static String resolve() {
        String encodedKey = System.getProperty(AesGcmUtil.KEY_PROPERTY);
        if (encodedKey != null) {
            return encodedKey;
        }

        encodedKey = System.getenv(AesGcmUtil.KEY_ENV);
        if (encodedKey != null) {
            return encodedKey;
        }

        Path keyFile = resolveKeyFile();
        if (Files.exists(keyFile, LinkOption.NOFOLLOW_LINKS)) {
            return readKeyFile(keyFile);
        }
        if (isCommunityDesktop()) {
            return createKeyFile(keyFile);
        }
        throw missingKey(keyFile);
    }

    static Path resolveKeyFile() {
        String configuredPath = System.getProperty(AesGcmUtil.KEY_FILE_PROPERTY);
        if (configuredPath != null) {
            return configuredKeyFile(configuredPath);
        }
        configuredPath = System.getenv(AesGcmUtil.KEY_FILE_ENV);
        if (configuredPath != null) {
            return configuredKeyFile(configuredPath);
        }

        String userHome = System.getProperty("user.home");
        if (!hasText(userHome)) {
            throw new IllegalStateException("Unable to resolve Community encryption key file without user.home");
        }
        return Path.of(userHome, ".config", "chat2db-community", "encryption.key")
                .toAbsolutePath()
                .normalize();
    }

    private static synchronized String createKeyFile(Path keyFile) {
        Path temporaryFile = null;
        byte[] keyBytes = new byte[KEY_LENGTH_BYTES];
        try {
            Path keyDirectory = keyFile.getParent();
            if (keyDirectory == null) {
                throw keyFileFailure(keyFile);
            }
            boolean directoryExists = Files.exists(keyDirectory, LinkOption.NOFOLLOW_LINKS);
            Files.createDirectories(keyDirectory);
            if (!directoryExists) {
                setPosixPermissions(keyDirectory, DIRECTORY_PERMISSIONS);
            }

            Path lockFile = keyDirectory.resolve("." + keyFile.getFileName() + ".lock");
            try (FileChannel lockChannel = FileChannel.open(lockFile,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    FileLock ignored = lockChannel.lock()) {
                setOwnerOnlyPermissions(lockFile);
                if (Files.exists(keyFile, LinkOption.NOFOLLOW_LINKS)) {
                    return readKeyFile(keyFile);
                }

                SECURE_RANDOM.nextBytes(keyBytes);
                String encodedKey = Base64.getEncoder().encodeToString(keyBytes);
                temporaryFile = createSecureTemporaryFile(keyDirectory);
                Files.writeString(temporaryFile, encodedKey + System.lineSeparator(), StandardCharsets.UTF_8);
                setOwnerOnlyPermissions(temporaryFile);
                try {
                    moveKeyFile(temporaryFile, keyFile);
                } catch (FileAlreadyExistsException exception) {
                    return readKeyFile(keyFile);
                }
                temporaryFile = null;
                setOwnerOnlyPermissions(keyFile);
                return encodedKey;
            }
        } catch (IOException exception) {
            throw keyFileFailure(keyFile);
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static String readKeyFile(Path keyFile) {
        try {
            if (Files.isSymbolicLink(keyFile)
                    || !Files.isRegularFile(keyFile, LinkOption.NOFOLLOW_LINKS)) {
                throw keyFileFailure(keyFile);
            }
            return Files.readString(keyFile, StandardCharsets.UTF_8).trim();
        } catch (IOException exception) {
            throw keyFileFailure(keyFile);
        }
    }

    private static Path configuredKeyFile(String configuredPath) {
        if (!hasText(configuredPath)) {
            throw new IllegalStateException("Community encryption key file path must not be blank");
        }
        return Path.of(configuredPath.trim()).toAbsolutePath().normalize();
    }

    private static void moveKeyFile(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target);
        }
    }

    private static Path createSecureTemporaryFile(Path directory) throws IOException {
        FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions
                .asFileAttribute(FILE_PERMISSIONS);
        try {
            return Files.createTempFile(directory, ".encryption-key-", ".tmp", permissions);
        } catch (UnsupportedOperationException exception) {
            Path temporaryFile = Files.createTempFile(directory, ".encryption-key-", ".tmp");
            setOwnerOnlyPermissions(temporaryFile);
            return temporaryFile;
        }
    }

    private static void setOwnerOnlyPermissions(Path path) throws IOException {
        setPosixPermissions(path, FILE_PERMISSIONS);
        if (Files.getFileAttributeView(path, java.nio.file.attribute.PosixFileAttributeView.class) == null) {
            path.toFile().setReadable(false, false);
            path.toFile().setWritable(false, false);
            path.toFile().setExecutable(false, false);
            path.toFile().setReadable(true, true);
            path.toFile().setWritable(true, true);
        }
    }

    private static void setPosixPermissions(Path path, Set<PosixFilePermission> permissions) throws IOException {
        try {
            Files.setPosixFilePermissions(path, permissions);
        } catch (UnsupportedOperationException ignored) {
        }
    }

    private static boolean isCommunityDesktop() {
        return COMMUNITY_RUNTIME_MODE.equalsIgnoreCase(System.getProperty("chat2db.runtime.mode"))
                && DESKTOP_MODE.equalsIgnoreCase(System.getProperty("chat2db.mode"));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static IllegalStateException missingKey(Path keyFile) {
        return new IllegalStateException(
                "Community encryption key is required. Configure " + AesGcmUtil.KEY_PROPERTY
                        + " or " + AesGcmUtil.KEY_ENV + ", or initialize " + keyFile);
    }

    private static IllegalStateException keyFileFailure(Path keyFile) {
        return new IllegalStateException("Unable to initialize Community encryption key file: " + keyFile);
    }
}
