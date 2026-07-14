package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.sql.SqlContextParser;
import ai.chat2db.community.domain.api.model.sql.SqlHover;
import ai.chat2db.community.domain.api.model.sql.SqlKeyword;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlContextParserRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlHoverRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlKeywordRequest;

import java.util.List;

/**
 * Provides SQL keyword, context, quick-parse, and hover parsing contracts for editors.
 */
public interface IDbSqlParserService {

    /**
     * Returns SQL keywords for the requested database type and editor context.
     *
     * @param dbSqlKeywordRequest keyword lookup parameters.
     * @return keyword metadata for the requested SQL dialect.
     */
    SqlKeyword getKeywords(DbSqlKeywordRequest dbSqlKeywordRequest);

    /**
     * Parses SQL context around the editor cursor.
     *
     * @param dbSqlContextParserRequest SQL context parser parameters.
     * @return parsed SQL context for editor features.
     */
    SqlContextParser contextParser(DbSqlContextParserRequest dbSqlContextParserRequest);

    /**
     * Returns hover information for SQL tokens under the cursor.
     *
     * @param dbSqlHoverRequest SQL hover parameters.
     * @return list of SQL hover.
     */
    List<SqlHover> sqlHover(DbSqlHoverRequest dbSqlHoverRequest);

    /**
     * Performs a lightweight SQL context parse for completion flows.
     *
     * @param dbSqlContextParserRequest SQL context parser parameters.
     * @return parsed SQL context for editor features.
     */
    SqlContextParser quickParser(DbSqlContextParserRequest dbSqlContextParserRequest);
}
