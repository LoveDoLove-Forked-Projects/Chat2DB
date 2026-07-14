package ai.chat2db.community.web.api.config.mcp.interceptor;

import ai.chat2db.community.tools.util.SystemSettingsUtil;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.model.HeaderAndCookies;
import ai.chat2db.community.tools.util.ConfigUtils;
import ai.chat2db.community.tools.util.ContextUtils;
import ai.chat2db.community.web.api.model.http.CookieUtil;
import ai.chat2db.community.web.api.config.mcp.security.McpSecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class McpAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        buildMcpContext();
        log.info("MCP request start method={}, uri={}, query={}, remoteAddr={}, userAgent={}, contentType={}, hasMcpToken={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                request.getContentType(),
                request.getHeader(McpSecurityConstants.MCP_AUTH_HEADER) != null);
        if (ConfigUtils.isDesktop() && ConfigUtils.isShowGUI()) {
            String actualToken = request.getHeader(McpSecurityConstants.MCP_AUTH_HEADER);
            String expectedToken = SystemSettingsUtil.getOrCreateMcpAuthToken();
            if (expectedToken.equals(actualToken)) {
                return true;
            }
            log.warn("MCP request rejected method={}, uri={}, reason=invalid_desktop_token, remoteAddr={}, hasMcpToken={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    actualToken != null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"success":false,"errorCode":"common.mcpUnauthorized","errorMessage":"MCP access requires a valid desktop MCP token."}
                    """);
            return false;
        }
        if (ConfigUtils.isLocalPersistence() || CookieUtil.getUserIdCookie() != null) {
            return true;
        }
        log.warn("MCP request rejected method={}, uri={}, reason=login_required, remoteAddr={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"success":false,"errorCode":"common.needLoggedIn","errorMessage":"MCP access requires login credentials."}
                """);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            log.info("MCP request end method={}, uri={}, status={}, error={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    ex == null ? null : ex.getMessage());
        } finally {
            ContextUtils.removeContext();
        }
    }

    private void buildMcpContext() {
        Pair<String, String> pair = CookieUtil.getOrganizationInfo();
        String organizationToken = pair.getFirst();
        String organizationString = pair.getSecond();

        Long organizationId = null;
        if (StringUtils.isNumeric(organizationString)) {
            organizationId = Long.parseLong(organizationString);
        }

        HeaderAndCookies headerAndCookies = null;
        if (!ConfigUtils.isDesktop()) {
            headerAndCookies = CookieUtil.getHeaderAndCookies();
            if (organizationId != null) {
                ContextUtils.setHeaderAndCookies(organizationId, headerAndCookies);
            }
        }

        Context context = Context.builder()
                .organizationToken(organizationToken)
                .organizationId(organizationId)
                .headerAndCookies(headerAndCookies)
                .build();
        ContextUtils.setContext(context);
        log.info("MCP context prepared organizationId={}, hasOrganizationToken={}, hasHeaderAndCookies={}",
                organizationId,
                StringUtils.isNotBlank(organizationToken),
                headerAndCookies != null);
    }
}
