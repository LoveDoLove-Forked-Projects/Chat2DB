package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.db.DbExecuteResultEnhanceRequest;

/**
 * Enriches SQL execution results with datasource-specific metadata and display values.
 */
public interface IDbExecuteResultEnhanceService {

    /**
     * Enriches an execution response with additional result metadata in place.
     *
     * @param dbExecuteResultEnhanceRequest execution response enhancement parameters.
     */
    void enhance(DbExecuteResultEnhanceRequest dbExecuteResultEnhanceRequest);
}
