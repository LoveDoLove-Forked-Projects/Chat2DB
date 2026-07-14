package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.web.api.config.cli.support.CliControllerSupport;

import java.util.List;

import ai.chat2db.community.domain.api.service.cli.ICliMetadataService;
import ai.chat2db.community.web.api.config.cli.security.CliRuntimeOnly;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.cli.CliWebConverter;
import ai.chat2db.community.web.api.model.request.cli.CliConnectionRequest;
import ai.chat2db.community.web.api.model.request.cli.CliTableDetailRequest;
import ai.chat2db.community.web.api.model.request.cli.CliTablesListRequest;
import ai.chat2db.community.web.api.model.response.cli.CliColumnResponse;
import ai.chat2db.community.web.api.model.response.cli.CliDatabaseResponse;
import ai.chat2db.community.web.api.model.response.cli.CliIndexResponse;
import ai.chat2db.community.web.api.model.response.cli.CliPageResponse;
import ai.chat2db.community.web.api.model.response.cli.CliResult;
import ai.chat2db.community.web.api.model.response.cli.CliSchemaResponse;
import ai.chat2db.community.web.api.model.response.cli.CliTableResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes CLI metadata lookup endpoints.
 */
@ConnectionInfoAspect
@RestController
@CliRuntimeOnly
@RequestMapping("/api/cli/v1/metadata")
public class CliMetadataController extends CliControllerSupport {

    private final ICliMetadataService cliMetadataService;
    private final CliWebConverter cliWebConverter;

    public CliMetadataController(ICliMetadataService cliMetadataService, CliWebConverter cliWebConverter) {
        this.cliMetadataService = cliMetadataService;
        this.cliWebConverter = cliWebConverter;
    }

    /**
     * Handles databases for CLI metadata.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/metadata/databases/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI database response.
     */
    @PostMapping("/databases/list")
    public CliResult<List<CliDatabaseResponse>> databases(@Valid @RequestBody CliConnectionRequest request,
                                                    HttpServletRequest servletRequest) {
        return CliResult.ok(cliWebConverter.database2response(
                cliMetadataService.listDatabases(cliWebConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Handles schemas for CLI metadata.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/metadata/schemas/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI schema response.
     */
    @PostMapping("/schemas/list")
    public CliResult<List<CliSchemaResponse>> schemas(@Valid @RequestBody CliConnectionRequest request,
                                                HttpServletRequest servletRequest) {
        return CliResult.ok(cliWebConverter.schema2response(
                cliMetadataService.listSchemas(cliWebConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Handles tables for CLI metadata.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/metadata/tables/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI table response.
     */
    @PostMapping("/tables/list")
    public CliResult<CliPageResponse<CliTableResponse>> tables(@Valid @RequestBody CliTablesListRequest request,
                                                   HttpServletRequest servletRequest) {
        return CliResult.ok(cliWebConverter.page2response(
                cliMetadataService.listTables(cliWebConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Handles table detail for CLI metadata.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/metadata/tables/detail}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI table response.
     */
    @PostMapping("/tables/detail")
    public CliResult<CliTableResponse> tableDetail(@Valid @RequestBody CliTableDetailRequest request,
                                             HttpServletRequest servletRequest) {
        return CliResult.ok(cliWebConverter.table2response(
                cliMetadataService.tableDetail(cliWebConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Handles columns for CLI metadata.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/metadata/tables/columns}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI column response.
     */
    @PostMapping("/tables/columns")
    public CliResult<List<CliColumnResponse>> columns(@Valid @RequestBody CliTableDetailRequest request,
                                                HttpServletRequest servletRequest) {
        return CliResult.ok(cliWebConverter.column2response(
                cliMetadataService.columns(cliWebConverter.request2param(request))), requestId(servletRequest));
    }

    /**
     * Handles indexes for CLI metadata.
     * <p>
     * Endpoint: {@code POST /api/cli/v1/metadata/tables/indexes}.
     *
     * @param request request payload or query parameters for the operation.
     * @param servletRequest HTTP request context.
     * @return controller response containing CLI index response.
     */
    @PostMapping("/tables/indexes")
    public CliResult<List<CliIndexResponse>> indexes(@Valid @RequestBody CliTableDetailRequest request,
                                               HttpServletRequest servletRequest) {
        return CliResult.ok(cliWebConverter.index2response(
                cliMetadataService.indexes(cliWebConverter.request2param(request))), requestId(servletRequest));
    }
}
