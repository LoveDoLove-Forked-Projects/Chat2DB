package ai.chat2db.community.web.api.converter.cli;

import ai.chat2db.community.domain.api.model.cli.CliColumn;
import ai.chat2db.community.domain.api.model.cli.CliDatabase;
import ai.chat2db.community.domain.api.model.cli.CliIndex;
import ai.chat2db.community.domain.api.model.cli.CliPage;
import ai.chat2db.community.domain.api.model.cli.CliSchema;
import ai.chat2db.community.domain.api.model.cli.CliSqlColumn;
import ai.chat2db.community.domain.api.model.cli.CliTable;
import ai.chat2db.community.domain.api.model.request.cli.CliConnectionResolveRequest;
import ai.chat2db.community.web.api.model.request.cli.CliConnectionRequest;
import ai.chat2db.community.web.api.model.request.cli.CliSqlQueryRequest;
import ai.chat2db.community.web.api.model.request.cli.CliTableDetailRequest;
import ai.chat2db.community.web.api.model.request.cli.CliTablesListRequest;
import ai.chat2db.community.web.api.model.response.cli.CliColumnResponse;
import ai.chat2db.community.web.api.model.response.cli.CliDatabaseResponse;
import ai.chat2db.community.web.api.model.response.cli.CliIndexResponse;
import ai.chat2db.community.web.api.model.response.cli.CliPageResponse;
import ai.chat2db.community.web.api.model.response.cli.CliTableResponse;
import ai.chat2db.community.web.api.model.response.cli.CliSchemaResponse;
import ai.chat2db.community.web.api.model.response.cli.CliSqlColumnResponse;
import ai.chat2db.community.web.api.model.response.cli.CliSqlQueryResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class CliWebConverter {

    public CliConnectionResolveRequest request2param(CliConnectionRequest request) {
        CliConnectionResolveRequest param =
                new CliConnectionResolveRequest();
        fillConnectionParam(request, param);
        return param;
    }

    public ai.chat2db.community.domain.api.model.request.cli.CliTablesListRequest request2param(CliTablesListRequest request) {
        ai.chat2db.community.domain.api.model.request.cli.CliTablesListRequest param =
                new ai.chat2db.community.domain.api.model.request.cli.CliTablesListRequest();
        fillConnectionParam(request, param);
        param.setSearchKey(request.getSearchKey());
        return param;
    }

    public ai.chat2db.community.domain.api.model.request.cli.CliTableDetailRequest request2param(CliTableDetailRequest request) {
        ai.chat2db.community.domain.api.model.request.cli.CliTableDetailRequest param =
                new ai.chat2db.community.domain.api.model.request.cli.CliTableDetailRequest();
        fillConnectionParam(request, param);
        param.setTableName(request.getTableName());
        return param;
    }

    public ai.chat2db.community.domain.api.model.request.cli.CliSqlQueryRequest request2param(CliSqlQueryRequest request) {
        ai.chat2db.community.domain.api.model.request.cli.CliSqlQueryRequest param =
                new ai.chat2db.community.domain.api.model.request.cli.CliSqlQueryRequest();
        param.setDataSourceId(request.getDataSourceId());
        param.setDatabaseName(request.getDatabaseName());
        param.setSchemaName(request.getSchemaName());
        param.setPageNo(request.getPageNo());
        param.setPageSize(request.getPageSize());
        param.setRefresh(request.getRefresh());
        param.setSql(request.getSql());
        param.setTimeoutMs(request.getTimeoutMs());
        param.setResultSetId(request.getResultSetId());
        param.setIncludeRowNumber(request.getIncludeRowNumber());
        return param;
    }

    private void fillConnectionParam(CliConnectionRequest request,
                                     CliConnectionResolveRequest param) {
        param.setDataSourceId(request.getDataSourceId());
        param.setDatabaseName(request.getDatabaseName());
        param.setSchemaName(request.getSchemaName());
        param.setPageNo(request.getPageNo());
        param.setPageSize(request.getPageSize());
        param.setRefresh(request.getRefresh());
    }

    public CliPageResponse<CliTableResponse> page2response(CliPage<CliTable> page) {
        if (page == null) {
            return CliPageResponse.of(Collections.emptyList(), null, null, 0L);
        }
        return CliPageResponse.of(table2response(page.getItems()), page.getPageNo(), page.getPageSize(), page.getTotal());
    }

    public List<CliDatabaseResponse> database2response(List<CliDatabase> databases) {
        if (databases == null) {
            return Collections.emptyList();
        }
        return databases.stream().filter(Objects::nonNull).map(this::database2response).toList();
    }

    private CliDatabaseResponse database2response(CliDatabase database) {
        CliDatabaseResponse response = new CliDatabaseResponse();
        response.setName(database.getName());
        response.setComment(database.getComment());
        response.setCharset(database.getCharset());
        response.setCollation(database.getCollation());
        response.setOwner(database.getOwner());
        response.setSystem(database.getSystem());
        return response;
    }

    public List<CliSchemaResponse> schema2response(List<CliSchema> schemas) {
        if (schemas == null) {
            return Collections.emptyList();
        }
        return schemas.stream().filter(Objects::nonNull).map(this::schema2response).toList();
    }

    private CliSchemaResponse schema2response(CliSchema schema) {
        CliSchemaResponse response = new CliSchemaResponse();
        response.setName(schema.getName());
        response.setDatabaseName(schema.getDatabaseName());
        response.setComment(schema.getComment());
        response.setOwner(schema.getOwner());
        response.setSystem(schema.getSystem());
        return response;
    }

    public List<CliTableResponse> table2response(List<CliTable> tables) {
        if (tables == null) {
            return Collections.emptyList();
        }
        return tables.stream().filter(Objects::nonNull).map(this::table2response).toList();
    }

    public CliTableResponse table2response(CliTable table) {
        if (table == null) {
            return null;
        }
        CliTableResponse response = new CliTableResponse();
        response.setName(table.getName());
        response.setComment(table.getComment());
        response.setSchemaName(table.getSchemaName());
        response.setDatabaseName(table.getDatabaseName());
        response.setType(table.getType());
        response.setEngine(table.getEngine());
        response.setRows(table.getRows());
        response.setColumns(column2response(table.getColumns()));
        response.setIndexes(index2response(table.getIndexes()));
        return response;
    }

    public List<CliColumnResponse> column2response(List<CliColumn> columns) {
        if (columns == null) {
            return Collections.emptyList();
        }
        return columns.stream().filter(Objects::nonNull).map(this::column2response).toList();
    }

    private CliColumnResponse column2response(CliColumn column) {
        CliColumnResponse response = new CliColumnResponse();
        response.setName(column.getName());
        response.setTableName(column.getTableName());
        response.setColumnType(column.getColumnType());
        response.setDataType(column.getDataType());
        response.setDefaultValue(column.getDefaultValue());
        response.setAutoIncrement(column.getAutoIncrement());
        response.setComment(column.getComment());
        response.setPrimaryKey(column.getPrimaryKey());
        response.setSchemaName(column.getSchemaName());
        response.setDatabaseName(column.getDatabaseName());
        response.setColumnSize(column.getColumnSize());
        response.setDecimalDigits(column.getDecimalDigits());
        response.setOrdinalPosition(column.getOrdinalPosition());
        response.setNullable(column.getNullable());
        return response;
    }

    public List<CliIndexResponse> index2response(List<CliIndex> indexes) {
        if (indexes == null) {
            return Collections.emptyList();
        }
        return indexes.stream().filter(Objects::nonNull).map(this::index2response).toList();
    }

    private CliIndexResponse index2response(CliIndex index) {
        CliIndexResponse response = new CliIndexResponse();
        response.setName(index.getName());
        response.setTableName(index.getTableName());
        response.setType(index.getType());
        response.setUnique(index.getUnique());
        response.setComment(index.getComment());
        response.setSchemaName(index.getSchemaName());
        response.setDatabaseName(index.getDatabaseName());
        response.setMethod(index.getMethod());
        return response;
    }

    public CliSqlQueryResponse result2response(
            ai.chat2db.community.domain.api.model.cli.CliSqlQueryResponse result) {
        if (result == null) {
            return null;
        }
        CliSqlQueryResponse response = new CliSqlQueryResponse();
        response.setColumns(sqlColumn2response(result.getColumns()));
        response.setRows(result.getRows() == null ? Collections.emptyList() : result.getRows());
        response.setUpdateCount(result.getUpdateCount());
        response.setRowCount(result.getRowCount());
        response.setHasNextPage(result.getHasNextPage());
        response.setTruncated(result.getTruncated());
        response.setDuration(result.getDuration());
        response.setPageNo(result.getPageNo());
        response.setPageSize(result.getPageSize());
        response.setResultSetId(result.getResultSetId());
        return response;
    }

    private List<CliSqlColumnResponse> sqlColumn2response(List<CliSqlColumn> columns) {
        if (columns == null) {
            return Collections.emptyList();
        }
        return columns.stream().filter(Objects::nonNull).map(this::sqlColumn2response).toList();
    }

    private CliSqlColumnResponse sqlColumn2response(CliSqlColumn column) {
        CliSqlColumnResponse response = new CliSqlColumnResponse();
        response.setName(column.getName());
        response.setType(column.getType());
        response.setTableName(column.getTableName());
        return response;
    }
}
