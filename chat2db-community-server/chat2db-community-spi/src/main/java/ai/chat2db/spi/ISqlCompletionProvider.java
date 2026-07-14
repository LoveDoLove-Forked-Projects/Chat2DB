package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;


public interface ISqlCompletionProvider {

    /**
     * Completes SQL editor input for the dialect.
     *
     * @param dbSqlCompletionRequest completion request containing SQL text, cursor position, and metadata context.
     * @return completion result containing candidates, hints, status, and trace data.
     */
    SqlCompletionResponse complete(DbSqlCompletionRequest dbSqlCompletionRequest);

    /**
     * Creates an unsupported completion provider for a database type name.
     *
     * @param databaseType database type name whose completion is unsupported.
     * @return provider that always returns an unsupported completion result.
     */
    static ISqlCompletionProvider unsupported(String databaseType) {
        return request -> SqlCompletionResponse.unsupported(databaseType);
    }
}
