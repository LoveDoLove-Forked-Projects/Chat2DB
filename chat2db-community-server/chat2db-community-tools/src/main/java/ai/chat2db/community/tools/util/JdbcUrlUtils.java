package ai.chat2db.community.tools.util;

import org.apache.commons.lang3.StringUtils;

public final class JdbcUrlUtils {

    private JdbcUrlUtils() {
    }

    public static String resetUrl(String url, String type, String serviceType) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        if (!"LocalFile".equalsIgnoreCase(serviceType)) {
            return url;
        }
        if ("H2".equalsIgnoreCase(type) || "SQLite".equalsIgnoreCase(type)) {
            String userHome = System.getProperty("user.home");
            String osName = System.getProperty("os.name");
            if (osName != null && osName.toLowerCase().contains("win")) {
                userHome = userHome.replace("/", "\\");
            }
            return url.replace("~", userHome);
        }
        return url;
    }
}
