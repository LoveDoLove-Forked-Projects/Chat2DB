package ai.chat2db.community.web.api.config.cli.support;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public abstract class CliControllerSupport {

    protected String requestId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-Id");
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }
}
