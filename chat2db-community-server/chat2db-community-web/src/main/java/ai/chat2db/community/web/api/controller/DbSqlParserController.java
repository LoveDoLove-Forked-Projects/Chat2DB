package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.model.request.sql.DbSqlContextParserRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlHoverRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlKeywordRequest;
import ai.chat2db.community.domain.api.model.sql.SqlContextParser;
import ai.chat2db.community.domain.api.model.sql.SqlHover;
import ai.chat2db.community.domain.api.model.sql.SqlKeyword;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlCompletionGetRequest;
import ai.chat2db.community.domain.api.service.db.IDbSqlCompletionService;
import ai.chat2db.community.domain.api.service.db.IDbSqlParserService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.tools.wrapper.result.ListResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.db.DbWebConverter;
import ai.chat2db.community.web.api.model.request.db.SqlCompletionRequest;
import ai.chat2db.community.web.api.model.request.db.SqlContextParserRequest;
import ai.chat2db.community.web.api.model.request.db.SqlHoverRequest;
import ai.chat2db.community.web.api.model.request.db.SqlKeywordRequest;
import ai.chat2db.community.web.api.model.response.db.SqlCompletionResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Exposes SQL parser context, keyword, tip, and hover endpoints.
 */
@ConnectionInfoAspect
@RequestMapping("/api/sql_parser")
@RestController
public class DbSqlParserController {

    private final DbWebConverter dbWebConverter;
    private final IDbSqlParserService sqlParserService;
    private final IDbSqlCompletionService sqlCompletionService;

    public DbSqlParserController(DbWebConverter dbWebConverter,
            IDbSqlParserService sqlParserService,
            IDbSqlCompletionService sqlCompletionService) {
        this.dbWebConverter = dbWebConverter;
        this.sqlParserService = sqlParserService;
        this.sqlCompletionService = sqlCompletionService;
    }

    /**
     * Gets keywords.
     * <p>
     * Endpoint: {@code GET /api/sql_parser/get_keywords}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL keyword.
     */
    @GetMapping("/get_keywords")
    public DataResult<SqlKeyword> getKeywords(@Valid SqlKeywordRequest request) {
        DbSqlKeywordRequest sqlKeywordParam = dbWebConverter.request2param(request);
        SqlKeyword sqlKeyword = sqlParserService.getKeywords(sqlKeywordParam);
        return DataResult.of(sqlKeyword);
    }

    /**
     * Handles context parser for SQL parser hints.
     * <p>
     * Endpoint: {@code POST /api/sql_parser/context/parser}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL context parser.
     */
    @PostMapping("/context/parser")
    public DataResult<SqlContextParser> contextParser(@Valid @RequestBody SqlContextParserRequest request) {
        DbSqlContextParserRequest sqlContextParserParam = dbWebConverter.request2param(request);
        SqlContextParser sqlContextParser = sqlParserService.contextParser(sqlContextParserParam);
        return DataResult.of(sqlContextParser);
    }

    /**
     * Handles quick parser for SQL parser hints.
     * <p>
     * Endpoint: {@code POST /api/sql_parser/context/quick_parser}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL context parser.
     */
    @PostMapping("/context/quick_parser")
    public DataResult<SqlContextParser> quickParser(@Valid @RequestBody SqlContextParserRequest request) {
        DbSqlContextParserRequest sqlContextParserParam = dbWebConverter.request2param(request);
        SqlContextParser sqlContextParser = sqlParserService.quickParser(sqlContextParserParam);
        return DataResult.of(sqlContextParser);
    }

    /**
     * Handles SQL completion for SQL parser hints.
     * <p>
     * Endpoint: {@code POST /api/sql_parser/context/tip}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL completion response.
     */
    @PostMapping("/context/tip")
    public DataResult<SqlCompletionResponse> sqlCompletion(
            @Valid @RequestBody SqlCompletionRequest request) {
        DbSqlCompletionGetRequest sqlCompletionParam = dbWebConverter.request2completionParam(request);
        var result = sqlCompletionService.complete(sqlCompletionParam);
        return DataResult.of(SqlCompletionResponse.from(result));
    }

    /**
     * Handles SQL hover for SQL parser hints.
     * <p>
     * Endpoint: {@code POST /api/sql_parser/context/hover}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing SQL hover.
     */
    @PostMapping("/context/hover")
    public ListResult<SqlHover> sqlHover(@Valid @RequestBody SqlHoverRequest request) {
        DbSqlHoverRequest sqlHoverParam = dbWebConverter.request2param(request);
        List<SqlHover> sqlHovers = sqlParserService.sqlHover(sqlHoverParam);
        return ListResult.of(sqlHovers);
    }


}
