package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.parser.statement.Statement;


public interface ISqlBatchHandler {

    /**
     * Handles one parsed SQL statement.
     *
     * @param statement parsed SQL statement.
     */
    void handle(Statement statement);

    /**
     * Flushes buffered statements and releases handler state.
     *
     */
    void flush();
}
