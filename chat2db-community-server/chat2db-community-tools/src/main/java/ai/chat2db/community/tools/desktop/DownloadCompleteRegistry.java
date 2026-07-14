package ai.chat2db.community.tools.desktop;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class DownloadCompleteRegistry {

    private static final List<Consumer<String>> LISTENERS = new CopyOnWriteArrayList<>();

    private DownloadCompleteRegistry() {
    }

    public static void subscribe(Consumer<String> listener) {
        if (listener != null) {
            LISTENERS.add(listener);
        }
    }

    public static void publish(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }
        LISTENERS.forEach(listener -> listener.accept(filePath));
    }
}
