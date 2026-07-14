package ai.chat2db.community.tools.util;

import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.model.LoginUser;
import org.apache.commons.math3.util.Pair;

import java.util.Optional;


public final class RuntimeIdentityProvider {

    private RuntimeIdentityProvider() {
    }

    public static boolean hasFixedIdentity() {
        return ConfigUtils.isCommunity();
    }

    public static Optional<LoginUser> loginUser() {
        if (ConfigUtils.isCommunity()) {
            return Optional.of(CommunityIdentity.loginUser());
        }
        return Optional.empty();
    }

    public static Optional<Long> userId() {
        if (ConfigUtils.isCommunity()) {
            return Optional.of(CommunityIdentity.USER_ID);
        }
        return Optional.empty();
    }

    public static Optional<Context> context() {
        if (ConfigUtils.isCommunity()) {
            return Optional.of(CommunityIdentity.context());
        }
        return Optional.empty();
    }

    public static Optional<Pair<String, String>> organizationInfo() {
        if (ConfigUtils.isCommunity()) {
            return Optional.of(new Pair<>(CommunityIdentity.ORGANIZATION_TOKEN, String.valueOf(CommunityIdentity.ORGANIZATION_ID)));
        }
        return Optional.empty();
    }

    public static Context enforce(Context context) {
        if (context == null || !hasFixedIdentity()) {
            return context;
        }
        context.setLoginUser(CommunityIdentity.loginUser());
        context.setOrganizationId(CommunityIdentity.ORGANIZATION_ID);
        context.setOrganizationToken(CommunityIdentity.ORGANIZATION_TOKEN);
        context.setToken(CommunityIdentity.IDENTITY_SOURCE);
        return context;
    }
}
