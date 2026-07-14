package ai.chat2db.community.domain.core.impl.cli;

import java.util.List;

import ai.chat2db.community.domain.api.model.PageResponse;
import ai.chat2db.community.domain.api.model.cli.CliColumn;
import ai.chat2db.community.domain.api.model.cli.CliDatabase;
import ai.chat2db.community.domain.api.model.cli.CliIndex;
import ai.chat2db.community.domain.api.model.cli.CliPage;
import ai.chat2db.community.domain.api.model.cli.CliSchema;
import ai.chat2db.community.domain.api.model.cli.CliTable;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.request.cli.CliConnectionResolveRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliTableDetailRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliTablesListRequest;
import ai.chat2db.community.domain.api.model.request.datasource.DbDatabaseQueryAllRequest;
import ai.chat2db.community.domain.api.model.request.db.DbSchemaQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTablePageQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.DbTableQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.TableSelector;
import ai.chat2db.community.domain.api.service.db.IDbDatabaseService;
import ai.chat2db.community.domain.api.service.db.IDbTableService;
import ai.chat2db.community.domain.api.service.cli.ICliMetadataService;
import ai.chat2db.community.domain.core.converter.CliMetadataDomainConverter;
import org.springframework.stereotype.Service;

@Service
public class CliMetadataServiceImpl implements ICliMetadataService {

    private final IDbDatabaseService databaseService;
    private final IDbTableService tableService;
    private final CliMetadataDomainConverter cliMetadataConverter;

    public CliMetadataServiceImpl(IDbDatabaseService databaseService, IDbTableService tableService,
            CliMetadataDomainConverter cliMetadataConverter) {
        this.databaseService = databaseService;
        this.tableService = tableService;
        this.cliMetadataConverter = cliMetadataConverter;
    }

    @Override
    public List<CliDatabase> listDatabases(CliConnectionResolveRequest request) {
        DbDatabaseQueryAllRequest param = DbDatabaseQueryAllRequest.builder()
                .dataSourceId(request.getDataSourceId())
                .refresh(request.refresh())
                .build();
        return cliMetadataConverter.database2response(databaseService.queryAll(param));
    }

    @Override
    public List<CliSchema> listSchemas(CliConnectionResolveRequest request) {
        DbSchemaQueryRequest param = DbSchemaQueryRequest.builder()
                .dataSourceId(request.getDataSourceId())
                .dataBaseName(request.getDatabaseName())
                .refresh(request.refresh())
                .build();
        return cliMetadataConverter.schema2response(databaseService.querySchema(param));
    }

    @Override
    public CliPage<CliTable> listTables(CliTablesListRequest request) {
        DbTablePageQueryRequest param = DbTablePageQueryRequest.builder()
                .dataSourceId(request.getDataSourceId())
                .databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName())
                .searchKey(request.getSearchKey())
                .pageNo(request.safePageNo())
                .pageSize(request.safePageSize())
                .refresh(request.refresh())
                .build();
        TableSelector selector = new TableSelector();
        selector.setColumnList(false);
        selector.setIndexList(false);
        PageResponse<Table> result = tableService.pageQuery(param, selector);
        return CliPage.of(cliMetadataConverter.table2response(result.getData(), false), result.getPageNo(),
                result.getPageSize(), result.getTotal());
    }

    @Override
    public CliTable tableDetail(CliTableDetailRequest request) {
        DbTableQueryRequest param = tableQueryParam(request);
        TableSelector selector = new TableSelector();
        selector.setColumnList(true);
        selector.setIndexList(true);
        return cliMetadataConverter.table2response(tableService.query(param, selector), true);
    }

    @Override
    public List<CliColumn> columns(CliTableDetailRequest request) {
        return cliMetadataConverter.column2response(tableService.queryColumns(tableQueryParam(request)));
    }

    @Override
    public List<CliIndex> indexes(CliTableDetailRequest request) {
        return cliMetadataConverter.index2response(tableService.queryIndexes(tableQueryParam(request)));
    }

    private DbTableQueryRequest tableQueryParam(CliTableDetailRequest request) {
        return DbTableQueryRequest.builder()
                .dataSourceId(request.getDataSourceId())
                .databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName())
                .tableName(request.getTableName())
                .refresh(request.refresh())
                .build();
    }

}
