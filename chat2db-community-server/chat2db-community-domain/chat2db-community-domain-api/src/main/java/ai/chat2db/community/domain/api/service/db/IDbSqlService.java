package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlExecuteWithConnectionRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlFormatRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlValidSelectRequest;

import java.sql.SQLException;
import java.util.List;

public interface IDbSqlService {

    /**
     * Formats SQL text for a database type.
     *
     * @param dbSqlFormatRequest SQL formatting parameters.
     * @return formatted SQL text.
     */
    String format(DbSqlFormatRequest dbSqlFormatRequest);

    /**
     * Checks whether SQL text is a valid select statement.
     *
     * @param dbSqlValidSelectRequest select validation parameters.
     * @return true when the SQL is a valid select statement; false otherwise.
     */
    Boolean validSelect(DbSqlValidSelectRequest dbSqlValidSelectRequest);

    /**
     * Removes comments from SQL text.
     *
     * @param sql SQL text to clean.
     * @param dbType database type code used to select dialect-specific parsing.
     * @return SQL text without comments.
     */
    String removeComment(String sql, String dbType);

    /**
     * Parses SQL text into simple statements.
     *
     * @param sql SQL text to parse.
     * @param dbType database type code used to select dialect-specific parsing.
     * @return parsed SQL statements in source order.
     */
    List<SimpleSqlStatement> parseStatements(String sql, String dbType);

    /**
     * Parses SQL text and validates table statements.
     *
     * @param sql SQL text to parse.
     * @param dbType database type code used to select dialect-specific parsing.
     * @return parsed and validated table statements in source order.
     */
    List<SimpleSqlStatement> parseAndValidTableStatements(String sql, String dbType);

    /**
     * Returns the inherited database type for a database type.
     *
     * @param dbType database type code.
     * @return inherited database type code, or the supplied type when no parent exists.
     */
    String getInheritedType(String dbType);

    /**
     * Converts an execution response into Markdown.
     *
     * @param result execution response to render.
     * @return Markdown text.
     */
    String result2Markdown(ExecuteResponse result);

    /**
     * Executes SQL with an existing JDBC connection and optional pagination.
     *
     * @param dbSqlExecuteWithConnectionRequest SQL execution parameters.
     * @return SQL execution response.
     * @throws SQLException when JDBC execution fails.
     */
    ExecuteResponse executeWithConnection(DbSqlExecuteWithConnectionRequest dbSqlExecuteWithConnectionRequest) throws SQLException;
}
