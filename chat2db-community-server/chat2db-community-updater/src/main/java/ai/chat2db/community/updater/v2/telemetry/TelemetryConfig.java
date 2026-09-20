package ai.chat2db.community.updater.v2.telemetry;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

/**
 * Fixed Umami endpoint and identifiers for the desktop usage reporting that rides along update checks.
 *
 * <p>The website id is a public value (it is embedded in the published tracking snippet) and is not a
 * secret, so it stays in source instead of being injected at package time.</p>
 */
public final class TelemetryConfig {

    public static final String ENDPOINT = "https://um.ottermind.ai/api/send";

    public static final String WEBSITE_ID = "51e9aab7-a6cd-44fb-8aa3-1db200cfaec7";

    public static final String HOSTNAME = "chat2db-desktop";

    public static final String PAGE_URL = "/update-check";

    public static final String EVENT_NAME = "update_check";

    /**
     * Umami derives the environment from the user agent, so the desktop agent keeps a parseable
     * platform prefix and appends the product token. The version stays fixed on purpose: the agent
     * feeds the session hash, so changing it would split a device into new sessions.
     */
    private static final String AGENT_MACOS = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
        + "AppleWebKit/537.36 (KHTML, like Gecko) Chat2DB-%s/1.0";

    private static final String AGENT_WINDOWS = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        + "AppleWebKit/537.36 (KHTML, like Gecko) Chat2DB-%s/1.0";

    private static final String AGENT_LINUX = "Mozilla/5.0 (X11; Linux x86_64) "
        + "AppleWebKit/537.36 (KHTML, like Gecko) Chat2DB-%s/1.0";

    /** The reporting request must never delay or fail an update check. */
    public static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3L);

    /** Device id and Umami session cache are shared by Community, Pro and Local. */
    public static final String STORE_DIRECTORY = ".chat2db-shared";

    public static final String STORE_FILE = "telemetry.json";

    /** Endpoint override, used by tests and by pointing a build at a staging instance. */
    public static final String ENDPOINT_PROPERTY = "chat2db.telemetry.endpoint";

    /** Store override, used by tests so they never touch the real user profile. */
    public static final String STORE_PROPERTY = "chat2db.telemetry.store";

    private TelemetryConfig() {
    }

    public static String endpoint() {
        String override = System.getProperty(ENDPOINT_PROPERTY, "");
        return override.isBlank() ? ENDPOINT : override;
    }

    public static Path storeFile() {
        String override = System.getProperty(STORE_PROPERTY, "");
        if (!override.isBlank()) {
            return Path.of(override);
        }
        return Path.of(System.getProperty("user.home", "."), STORE_DIRECTORY, STORE_FILE);
    }

    public static String userAgent(String productLabel, String platformLabel) {
        String template = switch (platformLabel == null ? "" : platformLabel) {
            case "Windows" -> AGENT_WINDOWS;
            case "Linux" -> AGENT_LINUX;
            default -> AGENT_MACOS;
        };
        return String.format(Locale.ROOT, template, productLabel);
    }
}
