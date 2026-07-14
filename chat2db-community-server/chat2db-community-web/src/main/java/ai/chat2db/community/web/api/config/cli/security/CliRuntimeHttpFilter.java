package ai.chat2db.community.web.api.config.cli.security;

import java.io.IOException;

import ai.chat2db.community.web.api.util.CliRuntimeUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@CliRuntimeOnly
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CliRuntimeHttpFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        boolean cliApiPath = path != null && path.startsWith(CliRuntimeUtils.API_PREFIX);
        if (!CliRuntimeUtils.isCliRuntimeMode() && !cliApiPath) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!CliRuntimeUtils.isCliRuntimeMode() || !cliApiPath) {
            writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                    "{\"success\":false,\"error\":{\"code\":\"cli_runtime_http_locked\",\"message\":\"CLI runtime only exposes /api/cli/v1 endpoints.\",\"details\":{}},\"data\":null,\"requestId\":null}");
            return;
        }

        String expectedToken = CliRuntimeUtils.runtimeToken();
        if (StringUtils.isBlank(expectedToken)) {
            writeJson(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "{\"success\":false,\"error\":{\"code\":\"cli_runtime_token_missing\",\"message\":\"CLI runtime token is not configured.\",\"details\":{}},\"data\":null,\"requestId\":null}");
            return;
        }

        String actualToken = bearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (!expectedToken.equals(actualToken)) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "{\"success\":false,\"error\":{\"code\":\"cli_runtime_unauthorized\",\"message\":\"CLI runtime access requires a valid bearer token.\",\"details\":{}},\"data\":null,\"requestId\":null}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String bearerToken(String authorization) {
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).trim();
    }

    private void writeJson(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }
}
