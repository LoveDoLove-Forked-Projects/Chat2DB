package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.request.db.DbMetaDataQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.DbSchemaQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTableShowCreateRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTablePageQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTableQueryRequest;
import ai.chat2db.community.domain.api.service.db.IDbDatabaseService;
import ai.chat2db.community.domain.api.service.db.IDbTableService;
import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.tools.wrapper.result.ListResult;
import ai.chat2db.community.tools.wrapper.result.ListResultWithTotal;
import ai.chat2db.community.tools.wrapper.result.web.WebPageResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.web.api.converter.db.DbWebConverter;
import ai.chat2db.community.web.api.model.request.db.*;
import ai.chat2db.community.web.api.model.response.db.*;
import ai.chat2db.community.domain.api.model.metadata.*;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes relational DDL metadata, object listing, and generated SQL endpoints.
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/ddl")
@RestController
public class DbDdlController {

    private final IDbTableService tableService;
    private final DbWebConverter dbWebConverter;
    private final IDbDatabaseService databaseService;

    public DbDdlController(IDbTableService tableService,
            DbWebConverter dbWebConverter,
            IDbDatabaseService databaseService) {
        this.tableService = tableService;
        this.dbWebConverter = dbWebConverter;
        this.databaseService = databaseService;
    }

    /**
     * Lists relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return paged web result containing table response.
     */
    @GetMapping("/list")
    public WebPageResult<TableResponse> list(@Valid TableBriefQueryRequest request) {
        DbTablePageQueryRequest queryParam = dbWebConverter.tablePageRequest2param(request);

        PageResponse<Table> tableDTOPageResult = tableService.pageQuery(queryParam,
                dbWebConverter.tableSelector(false, false));
        List<TableResponse> tableResponses = dbWebConverter.tableDto2response(tableDTOPageResult.getData());

        return WebPageResult.of(tableResponses, tableDTOPageResult.getTotal(), request.getPageNo(),
                request.getPageSize());
    }

    /**
     * Handles schema list for relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/schema_list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing schema response.
     */
    @GetMapping("/schema_list")
    public ListResult<SchemaResponse> schemaList(@Valid DataSourceBaseRequest request) {
        DbSchemaQueryRequest queryParam = dbWebConverter.dataSourceRequest2schemaQuery(request);
        List<Schema> tableColumns = databaseService.querySchema(queryParam);
        List<SchemaResponse> tableResponses = dbWebConverter.schemaDto2response(tableColumns);
        return ListResult.of(tableResponses);
    }

    /**
     * Handles database schema list for relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/database_schema_list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing meta schema response.
     */
    @GetMapping("/database_schema_list")
    public DataResult<MetaSchemaResponse> databaseSchemaList(@Valid DataSourceBaseRequest request) {
        DbMetaDataQueryRequest queryParam = dbWebConverter.dataSourceRequest2metadataQuery(request);
        MetaSchema result = databaseService.queryDatabaseSchema(queryParam);
        MetaSchemaResponse schemaDto2response = dbWebConverter.metaSchemaDto2response(result);
        return DataResult.of(schemaDto2response);
    }


    /**
     * Handles column list for relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/column_list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing column responses and their total count.
     */
    @GetMapping("/column_list")
    public ListResultWithTotal<ColumnResponse> columnList(@Valid TableDetailQueryRequest request) {
        DbTableQueryRequest queryParam = dbWebConverter.tableRequest2param(request);
        List<TableColumn> tableColumns = tableService.queryColumns(queryParam);
        List<ColumnResponse> tableResponses = dbWebConverter.columnDto2response(tableColumns);
        return ListResultWithTotal.from(tableResponses);
    }

    /**
     * Handles index list for relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/index_list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing index responses and their total count.
     */
    @GetMapping("/index_list")
    public ListResultWithTotal<IndexResponse> indexList(@Valid TableDetailQueryRequest request) {
        DbTableQueryRequest queryParam = dbWebConverter.tableRequest2param(request);
        List<TableIndex> tableIndices = tableService.queryIndexes(queryParam);
        List<IndexResponse> indexResponses = dbWebConverter.indexDto2response(tableIndices);
        return ListResultWithTotal.from(indexResponses);
    }

    /**
     * Handles key list for relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/key_list}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing key responses and their total count.
     */
    @GetMapping("/key_list")
    public ListResultWithTotal<IndexResponse> keyList(@Valid TableDetailQueryRequest request) {
        DbTableQueryRequest queryParam = dbWebConverter.tableRequest2param(request);
        return ListResultWithTotal.from(dbWebConverter.indexDto2response(tableService.queryKeys(queryParam)));
    }

    /**
     * Exports relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/export}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing string.
     */
    @GetMapping("/export")
    public DataResult<String> export(@Valid DdlExportRequest request) {
        DbTableShowCreateRequest param = dbWebConverter.ddlExport2showCreate(request);
        return DataResult.of(tableService.showCreateTable(param));
    }

    /**
     * Creates example.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/create/example}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing string.
     */
    @GetMapping("/create/example")
    public DataResult<String> createExample(@Valid TableCreateDdlQueryRequest request) {
        return DataResult.of(tableService.createTableExample(request.getDbType()));
    }

    /**
     * Updates example.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/update/example}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing string.
     */
    @GetMapping("/update/example")
    public DataResult<String> updateExample(@Valid TableUpdateDdlQueryRequest request) {
        return DataResult.of(tableService.alterTableExample(request.getDbType()));
    }

    /**
     * Queries relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/query}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing table response.
     */
    @GetMapping("/query")
    public DataResult<TableResponse> query(@Valid TableDetailQueryRequest request) {
        DbTableQueryRequest queryParam = dbWebConverter.tableRequest2param(request);
        Table tableDTODataResult = tableService.query(queryParam, dbWebConverter.tableSelector(true, true));
        TableResponse tableResponse = dbWebConverter.tableDto2response(tableDTODataResult);
        return DataResult.of(tableResponse);
    }

    /**
     * Handles modify SQL for relational DDL metadata.
     * <p>
     * Endpoint: {@code GET /api/rdb/ddl/modify/sql}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing SQL response.
     */
    @GetMapping("/modify/sql")
    public ListResult<SqlResponse> modifySql(@Valid TableModifySqlRequest request) {
        return ListResult.of(tableService.buildSql(
                        request.getOldTable(),
                        request.getNewTable(), TableBuilderConfig.defaultConfig())
                .stream()
                .map(dbWebConverter::dto2response)
                .toList());
    }

    /**
     * Deletes relational DDL metadata.
     * <p>
     * Endpoint: {@code POST /api/rdb/ddl/delete}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/delete")
    public ActionResult delete(@Valid @RequestBody TableDeleteRequest request) {
        DbTableQueryRequest param = dbWebConverter.tableRequest2param(request);
        tableService.dropTable(param);
        return ActionResult.isSuccess();
    }
}
