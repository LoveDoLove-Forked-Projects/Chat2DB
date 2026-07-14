package ai.chat2db.community.web.api.adapter.mcp;

import ai.chat2db.community.domain.api.service.mcp.IMcpConfigService;
import ai.chat2db.community.tools.http.LocalCookie;
import ai.chat2db.community.tools.util.ConfigUtils;
import ai.chat2db.community.tools.util.SystemSettingsUtil;
import ai.chat2db.community.web.api.config.mcp.security.McpSecurityConstants;
import ai.chat2db.community.web.api.model.http.CookieUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import jakarta.servlet.http.Cookie;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Builds web MCP configuration from runtime web context.
 */
@Component
@Slf4j
public class McpConfigAdapter implements IMcpConfigService {

    private final Environment environment;

    private final ObjectProvider<ServletWebServerApplicationContext> webServerApplicationContextProvider;

    public McpConfigAdapter(Environment environment,
                            ObjectProvider<ServletWebServerApplicationContext> webServerApplicationContextProvider) {
        this.environment = environment;
        this.webServerApplicationContextProvider = webServerApplicationContextProvider;
    }

    @Override
    public String copyConfig() {
        String baseUrl = resolveBaseUrl();
        String mcpUrl = baseUrl + "/mcp";
        Map<String, String> headers = buildHeaders();
        String serverName = "chat2db";

        JSONObject server = new JSONObject();
        server.put("type", "streamable-http");
        server.put("url", mcpUrl);
        if (!headers.isEmpty()) {
            server.put("headers", headers);
        }

        JSONObject mcpServers = new JSONObject();
        mcpServers.put(serverName, server);

        JSONObject root = new JSONObject();
        root.put("mcpServers", mcpServers);
        log.info("Generated MCP config serverName={}, type=streamable-http, url={}, clientMode={}, desktop={}, gui={}, headers={}",
                serverName,
                mcpUrl,
                isDesktopGuiClient() ? "desktop-gui" : "web",
                ConfigUtils.isDesktop(),
                ConfigUtils.isShowGUI(),
                headers.keySet());
        return root.toJSONString(JSONWriter.Feature.PrettyFormat);
    }

    private String resolveBaseUrl() {
        String scheme = environment.getProperty("server.ssl.enabled", Boolean.class, false) ? "https" : "http";
        String host = StringUtils.defaultIfBlank(environment.getProperty("server.address"), "127.0.0.1");
        return scheme + "://" + host + ":" + resolveServerPort();
    }

    private Map<String, String> buildHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        if (isDesktopGuiClient()) {
            headers.put(McpSecurityConstants.MCP_AUTH_HEADER, SystemSettingsUtil.getOrCreateMcpAuthToken());
        }
        if (!isDesktopGuiClient()) {
            String cookieHeader = buildCookieHeader();
            if (cookieHeader != null && !cookieHeader.isBlank()) {
                headers.put("Cookie", cookieHeader);
            }
            String authorization = resolveAuthorizationHeader();
            if (authorization != null && !authorization.isBlank()) {
                headers.put(CookieUtil.AUTHORIZATION, authorization);
            }
        }
        return headers;
    }

    private String buildCookieHeader() {
        if (ConfigUtils.isDesktop()) {
            Map<String, String> cookieMap = new LinkedHashMap<>();
            putIfPresent(cookieMap, CookieUtil.CHAT2DB_USER_ID, LocalCookie.getCookie(CookieUtil.CHAT2DB_USER_ID));
            putIfPresent(cookieMap, CookieUtil.CHAT2DB_ORGANIZATION_TOKEN, LocalCookie.getCookie(CookieUtil.CHAT2DB_ORGANIZATION_TOKEN));
            putIfPresent(cookieMap, CookieUtil.CHAT2DB_ORGANIZATION_ID, LocalCookie.getCookie(CookieUtil.CHAT2DB_ORGANIZATION_ID));
            return cookieMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("; "));
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        Cookie[] cookies = attributes.getRequest().getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        Map<String, String> cookieMap = new LinkedHashMap<>();
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }
            String name = cookie.getName();
            if (CookieUtil.CHAT2DB_USER_ID.equals(name)
                    || CookieUtil.CHAT2DB_ORGANIZATION_TOKEN.equals(name)
                    || CookieUtil.CHAT2DB_ORGANIZATION_ID.equals(name)) {
                putIfPresent(cookieMap, name, cookie.getValue());
            }
        }
        return cookieMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    private void putIfPresent(Map<String, String> cookieMap, String key, String value) {
        if (value != null && !value.isBlank()) {
            cookieMap.put(key, value);
        }
    }

    private String resolveAuthorizationHeader() {
        if (ConfigUtils.isDesktop()) {
            return LocalCookie.getHeader(CookieUtil.AUTHORIZATION);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest().getHeader(CookieUtil.AUTHORIZATION);
    }

    private int resolveServerPort() {
        ServletWebServerApplicationContext webServerApplicationContext =
                webServerApplicationContextProvider.getIfAvailable();
        if (webServerApplicationContext != null && webServerApplicationContext.getWebServer() != null) {
            return webServerApplicationContext.getWebServer().getPort();
        }
        Integer port = environment.getProperty("local.server.port", Integer.class);
        if (port != null) {
            return port;
        }
        return environment.getProperty("server.port", Integer.class, 8080);
    }

    private boolean isDesktopGuiClient() {
        return ConfigUtils.isDesktop() && ConfigUtils.isShowGUI();
    }
}
