package ai.chat2db.community.jcef.handler.file;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cef.OS;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NativeFileChooser {
    private static final Logger LOGGER = Logger.getLogger(NativeFileChooser.class.getName());


    public static Pair<String, String> showOpenDialog(Frame parentFrame, String title, String initialDirectory, String initialFile, long maxSizeMB, String... extensions) {
        return showDialogInternal(parentFrame, title, initialDirectory, initialFile, maxSizeMB, extensions);
    }


    private static Pair<String, String> showDialogInternal(final Frame parentFrameParam,
                                                           final String titleParam,
                                                           final String initialDirectoryParam,
                                                           final String initialOrDefFileParam,
                                                           final long maxSizeMB,
                                                           final String... extensionsParam) {
        if (EventQueue.isDispatchThread()) {
            return createAndShowDialog(parentFrameParam, titleParam, initialDirectoryParam, initialOrDefFileParam, maxSizeMB, extensionsParam);
        } else {
            final Pair<String, String>[] result = new Pair[1];
            try {
                EventQueue.invokeAndWait(() -> {
                    result[0] = createAndShowDialog(parentFrameParam, titleParam, initialDirectoryParam, initialOrDefFileParam, maxSizeMB, extensionsParam);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "File dialog operation was interrupted.", e);
                return null;
            } catch (InvocationTargetException e) {
                LOGGER.log(Level.SEVERE, "Exception occurred while showing file dialog on EDT.", e.getTargetException());
                return null;
            }
            return result[0];
        }
    }


    private static Pair<String, String> createAndShowDialog(Frame parentFrame,
                                                            String title,
                                                            String initialDirectory,
                                                            String initialOrDefFile,
                                                            long maxSizeMB,
                                                            String... extensions) {
        if (OS.isLinux()) {
            return createAndShowDialogLinux(parentFrame, title, initialDirectory, initialOrDefFile, maxSizeMB, extensions);
        }
        FileDialog fd = new FileDialog(parentFrame, title, FileDialog.LOAD);
        if (initialDirectory != null && !initialDirectory.isEmpty()) {
            File dir = new File(initialDirectory);
            if (dir.isDirectory()) {
                fd.setDirectory(dir.getAbsolutePath());
            }
        }
        if (initialOrDefFile != null && !initialOrDefFile.isEmpty()) {
            fd.setFile(initialOrDefFile);
        }
        if (extensions != null && extensions.length > 0 && extensions[0] != null && !extensions[0].isEmpty()) {
            String filterExtensionsString = extensions[0].replace("*.", "").toLowerCase();
            final String[] individualExtensions = filterExtensionsString.split("[,;\\s]+");

            if (individualExtensions.length > 0 && !(individualExtensions.length == 1 && individualExtensions[0].isEmpty())) {
                final Set<String> extSet = new HashSet<>(Arrays.asList(individualExtensions));
                fd.setFilenameFilter(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        File currentFile = new File(dir, name);
                        if (currentFile.isDirectory()) {
                            return true;
                        }
                        long fileSizeBytes = currentFile.length();
                        long fileSizeMB = fileSizeBytes / (1024 * 1024);

                        if (maxSizeMB != 0 && fileSizeMB > maxSizeMB) {
                            return false;
                        }
                        if (extSet.isEmpty()) {
                            return true;
                        }
                        String lowerName = name.toLowerCase();
                        int lastDot = lowerName.lastIndexOf('.');
                        if (lastDot > 0 && lastDot < lowerName.length() - 1) {
                            String ext = lowerName.substring(lastDot + 1);
                            return extSet.contains(ext);
                        }
                        return false;
                    }
                });
            }
        }
        fd.setVisible(true);
        String filename = fd.getFile();
        String directory = fd.getDirectory();

        if (filename != null && directory != null) {
            return Pair.of(new File(directory, filename).getAbsolutePath(), filename);
        }
        return Pair.of(null, null);
    }


    public static Pair<String, String> createAndShowDialogLinux(Frame parentFrame,
                                                                       String title,
                                                                       String initialDirectory,
                                                                       String initialOrDefFile,
                                                                       long maxSizeMB,
                                                                       String... extensions) {
        assert SwingUtilities.isEventDispatchThread();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        if (initialDirectory != null && !initialDirectory.isEmpty()) {
            File dir = new File(initialDirectory);
            if (dir.isDirectory()) {
                chooser.setCurrentDirectory(dir);
            }
        }
        if (initialOrDefFile != null && !initialOrDefFile.isEmpty()) {
            chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), initialOrDefFile));
        }
        if (extensions != null && extensions.length > 0 && extensions[0] != null && !extensions[0].isEmpty()) {
            String filterExtensionsString = extensions[0].toLowerCase().replace("*.", "");
            final String[] individualExtensions = filterExtensionsString.split("[,;\\s]+");

            if (individualExtensions.length > 0 && !(individualExtensions.length == 1 && individualExtensions[0].isEmpty())) {
                final Set<String> extSet = new HashSet<>(Arrays.asList(individualExtensions));
                FileFilter customFilter = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        if (maxSizeMB > 0) {
                            long fileSizeMB = f.length() / (1024 * 1024);
                            if (fileSizeMB > maxSizeMB) {
                                return false;
                            }
                        }
                        String name = f.getName().toLowerCase();
                        int lastDot = name.lastIndexOf('.');
                        if (lastDot > 0 && lastDot < name.length() - 1) {
                            String ext = name.substring(lastDot + 1);
                            return extSet.contains(ext);
                        }

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return extensions[0];
                    }
                };

                chooser.setFileFilter(customFilter);
            }
        }
        int result = chooser.showOpenDialog(parentFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                return Pair.of(selectedFile.getAbsolutePath(), selectedFile.getName());
            }
        }
        return Pair.of(null, null);
    }

    public static Pair<String, String> openFileDialog(Frame parentFrame,
                                                      String initialDirectory,
                                                      long maxSizeMB,
                                                      String extension) {
        if (StringUtils.isBlank(initialDirectory)) {
            initialDirectory = System.getProperty("user.home");
        }
        if (StringUtils.isBlank(extension)) {
            extension = "*.";
        }
        return NativeFileChooser.showOpenDialog(
                parentFrame,
                null,
                initialDirectory,
                null,
                maxSizeMB,
                extension
        );
    }
}
