package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.er.ERPosition;

/**
 * Persists ER diagram positions.
 */
public interface IDbErPositionService {

    String getErPosition(Long dataSourceId, String databaseName, String schemaName);

    void savePosition(ERPosition request);
}
