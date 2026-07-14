package ai.chat2db.community.tools.network;

import ai.chat2db.community.tools.config.SystemSettingConstant;
import ai.chat2db.community.tools.util.SystemSettingsUtil;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.tools.enums.network.NetworkProxyModeEnum;
import ai.chat2db.community.tools.enums.network.NetworkProxyTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class NetworkProxyUtil {

    private static final String DEFAULT_NO_PROXY_HOSTS = "localhost|127.*|[::1]";
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    private static final String[] PROXY_PROPERTIES = {
            "http.proxyHost",
            "http.proxyPort",
            "https.proxyHost",
            "https.proxyPort",
            "http.nonProxyHosts",
            "socksProxyHost",
            "socksProxyPort",
            "java.net.socks.username",
            "java.net.socks.password"
    };

    private NetworkProxyUtil() {
    }

    public static NetworkProxySettings getSettings() {
        Object value = SystemSettingsUtil.getProperty(SystemSettingConstant.NETWORK_PROXY);
        if (value == null) {
            return defaultSettings();
        }
        try {
            NetworkProxySettings settings = normalize(NetworkProxySettingsConverter.toSettings(value));
            settings.setRestartRequired(false);
            return settings;
        } catch (IllegalArgumentException e) {
            log.warn("Read network proxy settings failed, fallback to default", e);
            return defaultSettings();
        }
    }

    public static NetworkProxySettings saveSettings(NetworkProxySettings settings) {
        NetworkProxySettings normalized = normalize(settings);
        validate(normalized);
        normalized.setRestartRequired(false);
        SystemSettingsUtil.setProperty(SystemSettingConstant.NETWORK_PROXY, normalized);
        applyToJvm(normalized);
        normalized.setRestartRequired(true);
        return normalized;
    }

    public static void applySavedSettingsToJvm() {
        applyToJvm(getSettings());
    }

    public static void applyToJvm(NetworkProxySettings settings) {
        NetworkProxySettings normalized = normalize(settings);
        clearManualProxyProperties();

        if (normalized.getMode() == NetworkProxyModeEnum.SYSTEM) {
            System.setProperty("java.net.useSystemProxies", "true");
            log.info("Applied system network proxy settings to JVM");
            return;
        }

        System.setProperty("java.net.useSystemProxies", "false");
        if (normalized.getMode() != NetworkProxyModeEnum.MANUAL) {
            log.info("Applied no-proxy network settings to JVM");
            return;
        }

        validate(normalized);
        String port = String.valueOf(normalized.getPort());
        if (normalized.getProxyType() == NetworkProxyTypeEnum.SOCKS) {
            System.setProperty("socksProxyHost", normalized.getHost());
            System.setProperty("socksProxyPort", port);
        } else {
            System.setProperty("http.proxyHost", normalized.getHost());
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyHost", normalized.getHost());
            System.setProperty("https.proxyPort", port);
            System.setProperty("http.nonProxyHosts", normalized.getNoProxyHosts());
        }
        log.info("Applied manual {} network proxy settings to JVM, host={}, port={}",
                normalized.getProxyType(), normalized.getHost(), normalized.getPort());
    }

    public static void checkConnection(NetworkProxySettings settings, String testUrl) throws IOException {
        NetworkProxySettings normalized = normalize(settings);
        validate(normalized);
        String effectiveUrl = StringUtils.defaultIfBlank(testUrl, "https://www.google.com");
        URL url = URI.create(effectiveUrl).toURL();
        URLConnection connection = openConnection(url, normalized);
        connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);

        if (connection instanceof HttpURLConnection httpConnection) {
            try {
                int responseCode = httpConnection.getResponseCode();
                if (responseCode < 200 || responseCode >= 400) {
                    throw new BusinessException("networkProxy.unexpectedResponseCode", new Object[]{responseCode});
                }
            } finally {
                httpConnection.disconnect();
            }
            return;
        }
        connection.connect();
    }

    public static NetworkProxySettings normalize(NetworkProxySettings settings) {
        NetworkProxySettings normalized = Objects.requireNonNullElseGet(settings, NetworkProxyUtil::defaultSettings);
        if (normalized.getMode() == null) {
            normalized.setMode(NetworkProxyModeEnum.NO_PROXY);
        }
        if (normalized.getProxyType() == null) {
            normalized.setProxyType(NetworkProxyTypeEnum.HTTP);
        }
        normalized.setHost(StringUtils.trimToEmpty(normalized.getHost()));
        normalized.setNoProxyHosts(normalizeNoProxyHosts(normalized.getNoProxyHosts()));
        return normalized;
    }

    public static void validate(NetworkProxySettings settings) {
        if (settings == null || settings.getMode() != NetworkProxyModeEnum.MANUAL) {
            return;
        }
        if (StringUtils.isBlank(settings.getHost())) {
            throw new BusinessException("networkProxy.hostRequired");
        }
        Integer port = settings.getPort();
        if (port == null || port < 1 || port > 65535) {
            throw new BusinessException("networkProxy.portRange");
        }
    }

    public static String toChromiumProxyServer(NetworkProxySettings settings) {
        String endpoint = settings.getHost() + ":" + settings.getPort();
        if (settings.getProxyType() == NetworkProxyTypeEnum.SOCKS) {
            return "socks5://" + endpoint;
        }
        return "http=" + endpoint + ";https=" + endpoint;
    }

    public static String toChromiumBypassList(String noProxyHosts) {
        return Arrays.stream(normalizeNoProxyHosts(noProxyHosts).split("\\|"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(";"));
    }

    private static URLConnection openConnection(URL url, NetworkProxySettings settings) throws IOException {
        if (settings.getMode() == NetworkProxyModeEnum.NO_PROXY) {
            return url.openConnection(Proxy.NO_PROXY);
        }
        if (settings.getMode() != NetworkProxyModeEnum.MANUAL) {
            return url.openConnection();
        }
        Proxy.Type proxyType = settings.getProxyType() == NetworkProxyTypeEnum.SOCKS ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
        Proxy proxy = new Proxy(proxyType, new InetSocketAddress(settings.getHost(), settings.getPort()));
        return url.openConnection(proxy);
    }

    private static NetworkProxySettings defaultSettings() {
        NetworkProxySettings settings = new NetworkProxySettings();
        settings.setMode(NetworkProxyModeEnum.NO_PROXY);
        settings.setProxyType(NetworkProxyTypeEnum.HTTP);
        settings.setHost("");
        settings.setPort(null);
        settings.setNoProxyHosts(DEFAULT_NO_PROXY_HOSTS);
        settings.setRestartRequired(false);
        return settings;
    }

    private static String normalizeNoProxyHosts(String noProxyHosts) {
        String value = StringUtils.defaultIfBlank(noProxyHosts, DEFAULT_NO_PROXY_HOSTS);
        return Arrays.stream(value.replace(",", "|")
                        .replace(";", "|")
                        .replace("\r", "|")
                        .replace("\n", "|")
                        .split("\\|"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining("|"));
    }

    private static void clearManualProxyProperties() {
        for (String property : PROXY_PROPERTIES) {
            System.clearProperty(property);
        }
    }
}
