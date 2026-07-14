package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.db.LargeValueToken;
import ai.chat2db.community.domain.api.model.request.db.DbLargeValueTokensAttachRequest;

/**
 * Manages large-value tokens attached to execution results.
 */
public interface IDbLargeValueTokenService {

    /**
     * Attaches large-value tokens to an execution response.
     *
     * @param dbLargeValueTokensAttachRequest large-value token attachment parameters.
     */
    void attachTokens(DbLargeValueTokensAttachRequest dbLargeValueTokensAttachRequest);

    /**
     * Returns a large-value token or rejects an invalid identifier.
     *
     * @param id large-value token identifier.
     * @return valid large-value token.
     */
    LargeValueToken requireValid(String id);
}
