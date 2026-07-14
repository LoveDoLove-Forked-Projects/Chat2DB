package ai.chat2db.community.web.api.config.log;

import java.io.IOException;

import ai.chat2db.community.tools.util.LogUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
public class LogOncePerRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws
        ServletException, IOException {
        try {
            if (StringUtils.isBlank(MDC.get(LogUtils.TRACE_ID))) {
                MDC.put(LogUtils.TRACE_ID, LogUtils.generateTraceId());
            }
            String clientIp = request.getHeader("X-Real-IP");
            if (clientIp == null) {
                clientIp = request.getHeader("X-Forwarded-For");
            }
            if (clientIp == null) {
                clientIp = request.getRemoteAddr();
            }
            MDC.put(LogUtils.CLIENT_IP, clientIp);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(LogUtils.TRACE_ID);
        }
    }
}
