package ai.chat2db.community.web.api.adapter.identity;

import ai.chat2db.community.domain.api.enums.RoleCodeEnum;
import ai.chat2db.community.domain.api.service.sys.IIdentityService;
import ai.chat2db.community.web.api.model.http.CookieUtil;
import org.springframework.stereotype.Component;

@Component
public class IdentityAdapter implements IIdentityService {

    @Override
    public Long currentUserId() {
        try {
            Long userId = CookieUtil.getUserIdCookie();
            return userId == null ? RoleCodeEnum.DESKTOP.getDefaultUserId() : userId;
        } catch (Exception e) {
            return RoleCodeEnum.DESKTOP.getDefaultUserId();
        }
    }
}
