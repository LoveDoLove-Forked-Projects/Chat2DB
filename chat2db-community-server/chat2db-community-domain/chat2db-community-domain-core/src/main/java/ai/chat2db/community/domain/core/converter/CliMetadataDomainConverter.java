package ai.chat2db.community.domain.core.converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ai.chat2db.community.domain.api.model.cli.CliColumn;
import ai.chat2db.community.domain.api.model.cli.CliDatabase;
import ai.chat2db.community.domain.api.model.cli.CliIndex;
import ai.chat2db.community.domain.api.model.cli.CliSchema;
import ai.chat2db.community.domain.api.model.cli.CliTable;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import org.springframework.stereotype.Component;

@Component
public class CliMetadataDomainConverter {

    public List<CliDatabase> database2response(List<Database> databases) {
        if (databases == null) {
            return Collections.emptyList();
        }
        return databases.stream().filter(Objects::nonNull).map(this::database2response).toList();
    }

    public CliDatabase database2response(Database database) {
        if (database == null) {
            return null;
        }
        CliDatabase response = new CliDatabase();
        response.setName(database.getName());
        response.setComment(database.getComment());
        response.setCharset(database.getCharset());
        response.setCollation(database.getCollation());
        response.setOwner(database.getOwner());
        return response;
    }

    public List<CliSchema> schema2response(List<Schema> schemas) {
        if (schemas == null) {
            return Collections.emptyList();
        }
        return schemas.stream().filter(Objects::nonNull).map(this::schema2response).toList();
    }

    public CliSchema schema2response(Schema schema) {
        if (schema == null) {
            return null;
        }
        CliSchema response = new CliSchema();
        response.setName(schema.getName());
        response.setDatabaseName(schema.getDatabaseName());
        response.setComment(schema.getComment());
        response.setOwner(schema.getOwner());
        return response;
    }

    public List<CliTable> table2response(List<Table> tables, boolean includeChildren) {
        if (tables == null) {
            return Collections.emptyList();
        }
        return tables.stream().filter(Objects::nonNull).map(table -> table2response(table, includeChildren)).toList();
    }

    public CliTable table2response(Table table, boolean includeChildren) {
        if (table == null) {
            return null;
        }
        CliTable response = new CliTable();
        response.setName(table.getName());
        response.setComment(table.getComment());
        response.setSchemaName(table.getSchemaName());
        response.setDatabaseName(table.getDatabaseName());
        response.setType(table.getType());
        response.setEngine(table.getEngine());
        response.setRows(table.getRows());
        if (includeChildren) {
            response.setColumns(column2response(table.getColumnList()));
            response.setIndexes(index2response(table.getIndexList()));
        }
        return response;
    }

    public List<CliColumn> column2response(List<TableColumn> columns) {
        if (columns == null) {
            return Collections.emptyList();
        }
        return columns.stream().filter(Objects::nonNull).map(this::column2response).toList();
    }

    public CliColumn column2response(TableColumn column) {
        if (column == null) {
            return null;
        }
        CliColumn response = new CliColumn();
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

    public List<CliIndex> index2response(List<TableIndex> indexes) {
        if (indexes == null) {
            return Collections.emptyList();
        }
        return indexes.stream().filter(Objects::nonNull).map(this::index2response).toList();
    }

    public CliIndex index2response(TableIndex index) {
        if (index == null) {
            return null;
        }
        CliIndex response = new CliIndex();
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
}
