package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.db.DbDlExecuteRequest;
import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;

/**
 * Converts SQL execution parameters into parsed command metadata.
 */
public interface IDbSqlCommandService {

    /**
     * Parses execution parameters into command metadata.
     *
     * @param dbDlExecuteRequest SQL execution parameters.
     * @return parsed command metadata.
     */
    SqlExecuteRequest toSqlExecuteRequest(DbDlExecuteRequest dbDlExecuteRequest);
}
