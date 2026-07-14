package ai.chat2db.community.web.api.util;

import org.apache.commons.lang3.StringUtils;

public final class CliRuntimeUtils {

    public static final String RUNTIME_MODE_PROPERTY = "chat2db.runtime.mode";
    public static final String RUNTIME_TOKEN_PROPERTY = "chat2db.cli.runtime.token";
    public static final String RUNTIME_TOKEN_ENV = "CHAT2DB_CLI_RUNTIME_TOKEN";
    public static final String API_PREFIX = "/api/cli/v1";

    private CliRuntimeUtils() {
    }

    public static boolean isCliRuntimeMode() {
        return "cli".equalsIgnoreCase(System.getProperty(RUNTIME_MODE_PROPERTY));
    }

    public static String runtimeToken() {
        String token = StringUtils.trimToNull(System.getenv(RUNTIME_TOKEN_ENV));
        if (token != null) {
            return token;
        }
        return StringUtils.trimToNull(System.getProperty(RUNTIME_TOKEN_PROPERTY));
    }
}
