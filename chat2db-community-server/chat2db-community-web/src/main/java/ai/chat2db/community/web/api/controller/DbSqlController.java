package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.service.db.IDbSqlService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.db.DbWebConverter;
import ai.chat2db.community.web.api.model.request.sql.SqlFormatRequest;
import ai.chat2db.community.web.api.model.request.sql.SqlValidSelectRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes SQL formatting and validation endpoints.
 */
@ConnectionInfoAspect
@RequestMapping("/api/sql")
@RestController
public class DbSqlController {

    private final IDbSqlService sqlService;
    private final DbWebConverter dbWebConverter;

    public DbSqlController(IDbSqlService sqlService, DbWebConverter dbWebConverter) {
        this.sqlService = sqlService;
        this.dbWebConverter = dbWebConverter;
    }

    /**
     * Lists SQL records.
     * <p>
     * Endpoint: {@code GET /api/sql/format}.
     *
     * @param sqlFormatRequest request payload or query parameters for the operation.
     * @return data result containing string.
     */
    @GetMapping("/format")
    public DataResult<String> format(@Valid SqlFormatRequest sqlFormatRequest) {
        return DataResult.of(sqlService.format(dbWebConverter.request2param(sqlFormatRequest)));
    }

    /**
     * Validates select.
     * <p>
     * Endpoint: {@code GET /api/sql/valid_select}.
     *
     * @param sqlValidSelectRequest request payload or query parameters for the operation.
     * @return data result containing boolean.
     */
    @GetMapping("/valid_select")
    public DataResult<Boolean> validSelect(@Valid SqlValidSelectRequest sqlValidSelectRequest) {
        return DataResult.of(sqlService.validSelect(dbWebConverter.request2param(sqlValidSelectRequest)));
    }
}
