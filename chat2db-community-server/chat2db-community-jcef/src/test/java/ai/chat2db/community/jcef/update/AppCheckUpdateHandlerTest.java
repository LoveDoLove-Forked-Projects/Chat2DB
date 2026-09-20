package ai.chat2db.community.jcef.update;

import ai.chat2db.community.jcef.handler.biz.update.AppCheckUpdateHandler;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import ai.chat2db.community.updater.v2.telemetry.TelemetryConfig;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.cef.callback.CefQueryCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Closed loop test for the usage reporting that rides along an update check: the JCEF handler runs a real
 * update check against a stub updater and the assembled report is captured by a stub HTTP server.
 */
class AppCheckUpdateHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @TempDir
    Path temporaryDirectory;

    private HttpServer server;

    private final List<CapturedRequest> captured = new ArrayList<>();

    private record CapturedRequest(String userAgent, String body) {
    }

    @BeforeEach
    void setUp() throws IOException {
        captured.clear();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/send", exchange -> {
            String body;
            try (InputStream stream = exchange.getRequestBody()) {
                body = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
            synchronized (captured) {
                captured.add(new CapturedRequest(exchange.getRequestHeaders().getFirst("User-Agent"), body));
            }
            byte[] response = "{\"cache\":\"stub-cache\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        System.setProperty(TelemetryConfig.ENDPOINT_PROPERTY,
            "http://127.0.0.1:" + server.getAddress().getPort() + "/api/send");
        System.setProperty(TelemetryConfig.STORE_PROPERTY, temporaryDirectory.resolve("telemetry.json").toString());
        System.setProperty("chat2db.runtime.mode", "community");
    }

    @AfterEach
    void tearDown() {
        DesktopUpdaterRegistry.resetForTests();
        System.clearProperty(TelemetryConfig.ENDPOINT_PROPERTY);
        System.clearProperty(TelemetryConfig.STORE_PROPERTY);
        System.clearProperty("chat2db.runtime.mode");
        server.stop(0);
    }

    @Test
    void reportsIdentifyAndEventWhenAnUpdateCheckRuns() throws Exception {
        DesktopUpdaterRegistry.register(new StubUpdater(new DesktopUpdateCheckResult(true, "5.3.8")));

        CallbackResult callback = check("{\"trigger\":\"startup\"}");

        assertEquals(0, callback.failureCount.get());
        assertTrue(callback.successResponse.get().contains("available"));
        CapturedRequest pageView = awaitPageView();
        assertTrue(pageView.body().contains("\"url\":\"/update-check\""));
        assertEquals("community", OBJECT_MAPPER.readTree(pageView.body()).path("payload").path("tag").asText());

        CapturedRequest event = awaitEvent();
        assertTrue(event.userAgent().startsWith("Mozilla/5.0 (Macintosh;"), event.userAgent());
        assertTrue(event.userAgent().contains("Chat2DB-Community/1.0"));
        assertTrue(event.body().contains("\"name\":\"update_check\""));
        assertTrue(event.body().contains("\"tag\":\"community\""));
        assertTrue(event.body().contains("\"trigger\":\"startup\""));
        assertTrue(event.body().contains("\"result\":\"available\""));
        assertTrue(event.body().contains("\"latestVersion\":\"5.3.8\""));
        assertTrue(event.body().contains("\"version\":\"5.3.7-dev\""));
        assertTrue(event.body().contains("\"website\":\"" + TelemetryConfig.WEBSITE_ID + "\""));
    }

    @Test
    void offlineActivationSendsNothing() throws Exception {
        DesktopUpdaterRegistry.register(new StubUpdater(DesktopUpdateCheckResult.notAvailable()));

        CallbackResult callback = check("{\"trigger\":\"manual\",\"offlineActivation\":true}");

        assertEquals(0, callback.failureCount.get());
        Thread.sleep(300L);
        synchronized (captured) {
            assertTrue(captured.isEmpty(), "offline activation must not report");
        }
    }

    @Test
    void missingRequestContextStillReportsNoUpdate() throws Exception {
        DesktopUpdaterRegistry.register(new StubUpdater(DesktopUpdateCheckResult.notAvailable()));

        CallbackResult callback = check(null);

        assertEquals(0, callback.failureCount.get());
        CapturedRequest event = awaitEvent();
        assertTrue(event.body().contains("\"result\":\"no_update\""));
        assertFalse(event.body().contains("latestVersion"));
    }

    private CallbackResult check(String message) throws Exception {
        ConsoleMessage consoleMessage = new ConsoleMessage();
        consoleMessage.setMessage(message);
        CallbackResult result = new CallbackResult();
        new AppCheckUpdateHandler().handle(consoleMessage, new ConsoleResult(), result.callback());
        return result;
    }

    /**
     * The identify request is sent once per JVM, so tests assert on the named update_check event; the
     * page view that feeds the overview metrics is reported before it.
     */
    private CapturedRequest awaitEvent() throws InterruptedException {
        return awaitBody("\"name\":\"update_check\"", "no telemetry event was reported");
    }

    private CapturedRequest awaitPageView() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000L;
        while (System.currentTimeMillis() < deadline) {
            synchronized (captured) {
                for (CapturedRequest request : captured) {
                    if (request.body().contains("\"type\":\"event\"") && !request.body().contains("\"name\"")) {
                        return request;
                    }
                }
            }
            Thread.sleep(50L);
        }
        throw new AssertionError("no telemetry page view was reported");
    }

    private CapturedRequest awaitBody(String marker, String failure) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000L;
        while (System.currentTimeMillis() < deadline) {
            synchronized (captured) {
                for (CapturedRequest request : captured) {
                    if (request.body().contains(marker)) {
                        return request;
                    }
                }
            }
            Thread.sleep(50L);
        }
        throw new AssertionError(failure);
    }

    private static final class CallbackResult {
        private final java.util.concurrent.atomic.AtomicReference<String> successResponse =
            new java.util.concurrent.atomic.AtomicReference<>();
        private final java.util.concurrent.atomic.AtomicInteger failureCount =
            new java.util.concurrent.atomic.AtomicInteger();

        private CefQueryCallback callback() {
            return new CefQueryCallback() {
                @Override
                public void success(String response) {
                    successResponse.set(response);
                }

                @Override
                public void failure(int errorCode, String errorMessage) {
                    failureCount.incrementAndGet();
                }
            };
        }
    }

    private static final class StubUpdater implements IDesktopUpdater {
        private final DesktopUpdateCheckResult result;

        private StubUpdater(DesktopUpdateCheckResult result) {
            this.result = result;
        }

        @Override
        public DesktopUpdateCheckResult appCheckUpdate() {
            return result;
        }

        @Override
        public String installedVersion() {
            return "5.3.7-dev";
        }

        @Override
        public boolean triggerDownload(ConsoleResult consoleResult) {
            return false;
        }

        @Override
        public boolean triggerInstallation() {
            return false;
        }

        @Override
        public boolean prepareRestart() {
            return false;
        }

        @Override
        public void exitCurrentProcessAfterResponse() {
        }
    }
}
