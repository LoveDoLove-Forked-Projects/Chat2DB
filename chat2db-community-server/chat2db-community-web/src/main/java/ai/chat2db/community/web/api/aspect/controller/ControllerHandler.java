package ai.chat2db.community.web.api.aspect.controller;

import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.model.HeaderAndCookies;
import ai.chat2db.community.tools.util.ConfigUtils;
import ai.chat2db.community.tools.util.ContextUtils;
import ai.chat2db.community.tools.util.RuntimeIdentityProvider;
import ai.chat2db.community.web.api.model.http.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@Order(10)
public class ControllerHandler {


    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public Object handle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            if (RuntimeIdentityProvider.hasFixedIdentity()) {
                ContextUtils.setContext(RuntimeIdentityProvider.context().orElseThrow());
                return proceedingJoinPoint.proceed();
            }

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
            return proceedingJoinPoint.proceed();
        } finally {
            ContextUtils.removeContext();
        }
    }


}
