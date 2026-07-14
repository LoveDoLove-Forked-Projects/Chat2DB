package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.web.api.config.cli.support.CliControllerSupport;

import ai.chat2db.community.domain.api.service.cli.ICliSqlService;
import ai.chat2db.community.web.api.config.cli.security.CliRuntimeOnly;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.cli.CliWebConverter;
import ai.chat2db.community.web.api.model.request.cli.CliSqlQueryRequest;
import ai.chat2db.community.web.api.model.response.cli.CliResult;
import ai.chat2db.community.web.api.model.response.cli.CliSqlQueryResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes CLI SQL execution endpoints.
 */
@ConnectionInfoAspect
@RestController
@CliRuntimeOnly
@RequestMapping("/api/cli/v1/sql")
public class CliSqlController extends CliControllerSupport {

    private final ICliSqlService cliSqlService;
    private final CliWebConverter cliWebConverter;

    public CliSqlController(ICliSqlService cliSqlService, CliWebConverter cliWebConverter) {
        this.cliSqlService = cliSqlService;
        this.cliWebConverter = cliWebConverter;
    }

    /**
     * Queries CLI SQL execution.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/sql/query}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI SQL query response.
     */
    @PostMapping("/query")
    public CliResult<CliSqlQueryResponse> query(@Valid @RequestBody CliSqlQueryRequest request,
                                          HttpServletRequest servletRequest) {
        ai.chat2db.community.domain.api.model.request.cli.CliSqlQueryRequest param = cliWebConverter.request2param(request);
        return CliResult.ok(cliWebConverter.result2response(cliSqlService.query(param)), requestId(servletRequest));
    }
}
