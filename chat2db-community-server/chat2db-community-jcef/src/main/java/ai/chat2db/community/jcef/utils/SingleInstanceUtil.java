package ai.chat2db.community.jcef.utils;

import ai.chat2db.community.jcef.event.manager.FileOpenEventManager;
import ai.chat2db.community.tools.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@Slf4j
public final class SingleInstanceUtil {

    private static final Path LOCK_FILE_PATH;
    private static final Path IPC_FILE_PATH;
    private static FileChannel lockChannel;
    private static FileLock fileLock;

    static {
        String lockFileName = "app.lock";
        String ipcFileName = "app.ipc";
        String basePath = ConfigUtils.getBasePath();
        LOCK_FILE_PATH = Paths.get(basePath, lockFileName);
        IPC_FILE_PATH = Paths.get(basePath, ipcFileName);
    }


    private SingleInstanceUtil() {}


    public static boolean registerInstance(String[] args, Consumer<String> argumentConsumer) {
        try {
            Files.createDirectories(LOCK_FILE_PATH.getParent());
            lockChannel = FileChannel.open(LOCK_FILE_PATH, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            fileLock = lockChannel.tryLock();

            if (fileLock == null) {
                log.info("Lock file is held by another instance. Sending arguments to it.");
                if (args != null && args.length > 0) {
                    sendArgumentToFirstInstance(args[0]);
                }
                lockChannel.close();
                return false;
            }
            log.info("Successfully acquired the instance lock.");
            addShutdownHook();
            startIpcListener(argumentConsumer);
            if (args.length > 0) {
                if (args[0].startsWith("chat2db-")) {
                    return true;
                }
                Path path = Paths.get(args[0]);
                if (Files.exists(path)) {
                    String filePath = path.toAbsolutePath().toString();
                    FileOpenEventManager.stashFileOpenEvent(filePath);
                }
            }
            return true;

        } catch (OverlappingFileLockException e) {
            log.info("Another instance is running (OverlappingFileLockException). Sending arguments.");
            if (args != null && args.length > 0) {
                sendArgumentToFirstInstance(args[0]);
            }
            return false;
        } catch (IOException e) {
            log.error("An I/O error occurred during single instance registration. Starting as a new instance.", e);
            return true;
        }
    }


    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (fileLock != null && fileLock.isValid()) {
                    fileLock.release();
                }
                if (lockChannel != null && lockChannel.isOpen()) {
                    lockChannel.close();
                }
                Files.deleteIfExists(LOCK_FILE_PATH);
                Files.deleteIfExists(IPC_FILE_PATH);
                log.info("Instance lock released and cleanup complete.");
            } catch (IOException e) {
                log.warn("Error during lock file cleanup.", e);
            }
        }));
    }


    private static void startIpcListener(Consumer<String> argumentConsumer) {
        new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path parentDir = IPC_FILE_PATH.getParent();
                parentDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
                log.info("IPC listener started, watching for changes to {}", IPC_FILE_PATH.getFileName());

                while (true) {
                    WatchKey key = watchService.take();
                    TimeUnit.MILLISECONDS.sleep(100);
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changedFile = (Path) event.context();
                        if (changedFile.getFileName().equals(IPC_FILE_PATH.getFileName())) {
                            try {
                                String argument = Files.readString(IPC_FILE_PATH);
                                if (StringUtils.isNotBlank(argument)) {
                                    log.info("Received argument from another instance: {}", argument);
                                    argumentConsumer.accept(argument);
                                }
                            } catch (IOException e) {
                                log.warn("Error reading IPC file.", e);
                            }
                        }
                    }
                    if (!key.reset()) {
                        log.warn("WatchKey is no longer valid. IPC listener is shutting down.");
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                log.error("IPC listener thread was interrupted or failed.", e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }


    private static void sendArgumentToFirstInstance(String argument) {
        try {
            Files.writeString(IPC_FILE_PATH, argument, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Successfully sent argument '{}' to the first instance.", argument);
        } catch (IOException e) {
            log.error("Failed to send argument to the first instance.", e);
        }
    }
}
