package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;

/**
 * Supplies metadata candidates used by SQL completion providers.
 */
public interface ISqlCompletionMetadataProvider {

    /**
     * Lists metadata candidates for a SQL completion request.
     *
     * @param dbSqlCompletionMetadataRequest metadata completion request parameters.
     * @return metadata candidates or an unsupported result for the request.
     */
    SqlCompletionMetadataResponse list(DbSqlCompletionMetadataRequest dbSqlCompletionMetadataRequest);

    /**
     * Checks whether this provider supports a completion candidate type.
     *
     * @param type completion candidate type to check.
     * @return true when the condition is met; false otherwise.
     */
    default boolean supports(String type) {
        return false;
    }

    /**
     * Returns a metadata provider that always reports unsupported requests.
     *
     * @return provider instance that returns unsupported metadata results.
     */
    static ISqlCompletionMetadataProvider unsupported() {
        return request -> SqlCompletionMetadataResponse.unsupported();
    }
}
