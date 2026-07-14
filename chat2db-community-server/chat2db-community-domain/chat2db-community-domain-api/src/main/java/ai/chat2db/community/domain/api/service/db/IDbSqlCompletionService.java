package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlCompletionGetRequest;

/**
 * Produces SQL completion candidates from editor context and datasource metadata.
 */
public interface IDbSqlCompletionService {

    /**
     * Builds SQL completion candidates for the supplied editor context.
     *
     * @param dbSqlCompletionGetRequest SQL completion request parameters.
     * @return completion result containing candidates and context metadata.
     */
    SqlCompletionResponse complete(DbSqlCompletionGetRequest dbSqlCompletionGetRequest);
}
