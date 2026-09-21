package ai.chat2db.community.updater.v2.telemetry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Local persistence for the desktop usage reporting: the device id and the Umami session cache token.
 *
 * <p>The file lives in the shared Chat2DB directory so all three desktop products report the same
 * device id. Every failure is tolerated: reporting must never affect the application.</p>
 */
public final class TelemetryStore {

    private final Path file;

    private final ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public TelemetryStore() {
        this(TelemetryConfig.storeFile());
    }

    public TelemetryStore(Path file) {
        this.file = file;
    }

    public synchronized String deviceId() {
        State state = read();
        if (state.deviceId() != null && !state.deviceId().isBlank()) {
            return state.deviceId();
        }
        String deviceId = DeviceIdProvider.resolve();
        write(new State(deviceId, state.cache()));
        return deviceId;
    }

    public synchronized String cache() {
        String cache = read().cache();
        return cache == null ? "" : cache;
    }

    public synchronized void saveCache(String cache) {
        if (cache == null || cache.isBlank()) {
            return;
        }
        write(new State(read().deviceId(), cache));
    }

    private State read() {
        if (!Files.isRegularFile(file)) {
            return new State(null, null);
        }
        try {
            State state = objectMapper.readValue(file.toFile(), State.class);
            return state == null ? new State(null, null) : state;
        } catch (IOException exception) {
            return new State(null, null);
        }
    }

    private void write(State state) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writeValue(file.toFile(), state);
        } catch (IOException ignored) {
            // Reporting state is best effort.
        }
    }

    record State(String deviceId, String cache) {
    }
}
