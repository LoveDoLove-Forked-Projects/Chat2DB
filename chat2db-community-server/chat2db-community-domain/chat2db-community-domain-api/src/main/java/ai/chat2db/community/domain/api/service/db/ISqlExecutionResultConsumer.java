package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.ResultCell;

import java.util.List;


public interface ISqlExecutionResultConsumer {

    /**
     * Notifies that a SQL statement is about to execute.
     *
     * @param sql executable SQL text.
     * @param originalSql original SQL text before execution rewriting.
     * @param comment statement comment associated with the SQL.
     */
    void statementStarted(String sql, String originalSql, String comment);

    /**
     * Notifies that a result set or update result has started.
     *
     * @param result execution response being populated.
     */
    void resultStarted(ExecuteResponse result);

    /**
     * Consumes a batch of result rows.
     *
     * @param result execution response associated with the rows.
     * @param rows result rows in fetch order.
     */
    void rows(ExecuteResponse result, List<List<ResultCell>> rows);

    /**
     * Notifies that a result set has finished.
     *
     * @param result completed execution response.
     */
    void resultFinished(ExecuteResponse result);

    /**
     * Notifies that an update count is available.
     *
     * @param result execution response containing the update count.
     */
    void updateCount(ExecuteResponse result);

    /**
     * Notifies that a SQL statement has finished.
     *
     * @param sql executable SQL text.
     * @param duration execution duration in milliseconds.
     */
    void statementFinished(String sql, long duration);
}
