package ai.chat2db.community.web.api.controller;

import java.util.List;

import ai.chat2db.community.domain.api.model.request.db.DbSchemaOperationRequest;
import ai.chat2db.community.domain.api.model.request.db.DbSchemaQueryRequest;
import ai.chat2db.community.domain.api.service.db.IDbDatabaseService;
import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.tools.wrapper.result.ListResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.web.api.converter.db.DbWebConverter;
import ai.chat2db.community.web.api.model.request.db.SchemaCreateRequest;
import ai.chat2db.community.web.api.model.request.db.UpdateSchemaRequest;
import ai.chat2db.community.web.api.model.response.db.SchemaResponse;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import ai.chat2db.community.domain.api.model.sql.Sql;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes relational schema metadata and SQL generation endpoints.
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/schema")
@RestController
public class DbSchemaController {

    private final DbWebConverter dbWebConverter;
    private final IDbDatabaseService databaseService;

    public DbSchemaController(DbWebConverter dbWebConverter,
            IDbDatabaseService databaseService) {
        this.dbWebConverter = dbWebConverter;
        this.databaseService = databaseService;
    }

    /**
     * Lists schemas.
     * <p>
     * Endpoint: {@code GET /api/rdb/schema/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing schema response.
     */
    @GetMapping("/list")
    public ListResult<SchemaResponse> list(@Valid DataSourceBaseRequest request) {
        DbSchemaQueryRequest queryParam = dbWebConverter.dataSourceRequest2schemaQuery(request);
        List<Schema> tableColumns = databaseService.querySchema(queryParam);
        List<SchemaResponse> tableResponses = dbWebConverter.schemaDto2response(tableColumns);
        return ListResult.of(tableResponses);
    }

    /**
     * Creates schema.
     * <p>
     * Endpoint: {@code POST /api/rdb/schema/create_schema_sql}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing SQL.
     */
    @PostMapping("/create_schema_sql")
    public DataResult<Sql> createSchema(@Valid @RequestBody SchemaCreateRequest request) {
        return DataResult.of(databaseService.createSchema(dbWebConverter.request2param(request)));
    }

    /**
     * Handles modify schema for schemas.
     * <p>
     * Endpoint: {@code POST /api/rdb/schema/modify_schema}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/modify_schema")
    public ActionResult modifySchema(@Valid @RequestBody UpdateSchemaRequest request) {
        DbSchemaOperationRequest param = dbWebConverter.request2param(request);
        databaseService.modifySchema(param);
        return ActionResult.isSuccess();
    }
}
