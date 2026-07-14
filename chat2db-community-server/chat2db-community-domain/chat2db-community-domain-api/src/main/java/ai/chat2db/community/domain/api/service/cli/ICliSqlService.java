package ai.chat2db.community.domain.api.service.cli;

import ai.chat2db.community.domain.api.model.cli.CliSqlQueryResponse;
import ai.chat2db.community.domain.api.model.request.cli.CliSqlQueryRequest;

/**
 * Exposes SQL execution for CLI callers.
 */
public interface ICliSqlService {

    /**
     * Executes a SQL query for CLI callers.
     *
     * @param cliSqlQueryRequest CLI SQL query parameters.
     * @return CLI SQL query response.
     */
    CliSqlQueryResponse query(CliSqlQueryRequest cliSqlQueryRequest);
}
