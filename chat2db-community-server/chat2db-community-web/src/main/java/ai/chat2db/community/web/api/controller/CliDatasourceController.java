package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.web.api.config.cli.support.CliControllerSupport;

import ai.chat2db.community.domain.api.service.cli.ICliDataSourceService;
import ai.chat2db.community.web.api.config.cli.security.CliRuntimeOnly;
import ai.chat2db.community.web.api.converter.cli.CliDatasourceConverter;
import ai.chat2db.community.web.api.model.request.cli.CliConnectionTestRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceCreateRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceDeleteRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceGetRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceListRequest;
import ai.chat2db.community.web.api.model.request.cli.CliDatasourceUpdateRequest;
import ai.chat2db.community.web.api.model.response.cli.CliConnectionTestResponse;
import ai.chat2db.community.web.api.model.response.cli.CliDatasourceResponse;
import ai.chat2db.community.web.api.model.response.cli.CliPageResponse;
import ai.chat2db.community.web.api.model.response.cli.CliResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes CLI datasource management endpoints.
 */
@RestController
@CliRuntimeOnly
@RequestMapping("/api/cli/v1/datasources")
public class CliDatasourceController extends CliControllerSupport {

    private final ICliDataSourceService cliDataSourceService;
    private final CliDatasourceConverter cliDatasourceConverter;

    public CliDatasourceController(ICliDataSourceService cliDataSourceService, CliDatasourceConverter cliDatasourceConverter) {
        this.cliDataSourceService = cliDataSourceService;
        this.cliDatasourceConverter = cliDatasourceConverter;
    }

    /**
     * Lists CLI datasources.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/datasources/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI datasource response.
     */
    @PostMapping("/list")
    public CliResult<CliPageResponse<CliDatasourceResponse>> list(@Valid @RequestBody CliDatasourceListRequest request,
                                                      HttpServletRequest servletRequest) {
        return CliResult.ok(cliDatasourceConverter.page2response(
                cliDataSourceService.list(cliDatasourceConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Gets CLI datasources.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/datasources/get}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI datasource response.
     */
    @PostMapping("/get")
    public CliResult<CliDatasourceResponse> get(@Valid @RequestBody CliDatasourceGetRequest request,
                                          HttpServletRequest servletRequest) {
        return CliResult.ok(cliDatasourceConverter.datasource2response(
                cliDataSourceService.get(request.getDataSourceId())), requestId(servletRequest));
    }

    /**
     * Handles connection test for CLI datasources.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/datasources/connection-test}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI connection test response.
     */
    @PostMapping("/connection-test")
    public CliResult<CliConnectionTestResponse> connectionTest(@RequestBody CliConnectionTestRequest request,
                                                         HttpServletRequest servletRequest) {
        return CliResult.ok(cliDatasourceConverter.result2response(
                cliDataSourceService.connectionTest(cliDatasourceConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Creates CLI datasources.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/datasources/create}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI datasource response.
     */
    @PostMapping("/create")
    public CliResult<CliDatasourceResponse> create(@Valid @RequestBody CliDatasourceCreateRequest request,
                                             HttpServletRequest servletRequest) {
        return CliResult.ok(cliDatasourceConverter.datasource2response(
                cliDataSourceService.create(cliDatasourceConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Updates CLI datasources.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/datasources/update}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI datasource response.
     */
    @PostMapping("/update")
    public CliResult<CliDatasourceResponse> update(@Valid @RequestBody CliDatasourceUpdateRequest request,
                                             HttpServletRequest servletRequest) {
        return CliResult.ok(cliDatasourceConverter.datasource2response(
                cliDataSourceService.update(cliDatasourceConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Deletes CLI datasources.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/datasources/delete}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing boolean.
     */
    @PostMapping("/delete")
    public CliResult<Boolean> delete(@Valid @RequestBody CliDatasourceDeleteRequest request,
                                     HttpServletRequest servletRequest) {
        cliDataSourceService.delete(request.getDataSourceId());
        return CliResult.ok(Boolean.TRUE, requestId(servletRequest));
    }
}
