package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.db.DbStreamingExecuteRequest;

import java.sql.SQLException;

/**
 * Executes SQL in streaming mode.
 */
public interface IDbSqlExecutionService {

    /**
     * Executes SQL and streams statement/result events to registered callbacks.
     *
     * @param dbStreamingExecuteRequest SQL streaming execution parameters.
     * @throws SQLException when JDBC execution fails.
     */
    void executeStreaming(DbStreamingExecuteRequest dbStreamingExecuteRequest) throws SQLException;
}
