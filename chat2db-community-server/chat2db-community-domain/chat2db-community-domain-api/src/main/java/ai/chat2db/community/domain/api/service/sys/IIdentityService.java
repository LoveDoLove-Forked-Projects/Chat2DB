package ai.chat2db.community.domain.api.service.sys;

/**
 * Resolves runtime identity information for business services.
 */
public interface IIdentityService {

    /**
     * Returns the current user id.
     *
     * @return current user id.
     */
    Long currentUserId();
}
