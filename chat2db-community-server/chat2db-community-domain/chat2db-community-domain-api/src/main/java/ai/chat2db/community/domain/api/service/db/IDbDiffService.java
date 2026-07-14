package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.datasource.DbConnectionDiffRequest;

/**
 * Builds SQL diff output between two database connection definitions.
 */
public interface IDbDiffService {

    /**
     * Builds a SQL diff between source and target connection definitions.
     *
     * @param sourceDbConnectionDiffRequest source connection definition used as the diff baseline.
     * @param targetDbConnectionDiffRequest target connection definition compared against the source.
     * @return generated diff SQL.
     */
    String diff(DbConnectionDiffRequest sourceDbConnectionDiffRequest,
            DbConnectionDiffRequest targetDbConnectionDiffRequest);
}
