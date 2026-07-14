package ai.chat2db.plugin.oscar;

import ai.chat2db.plugin.oscar.util.OscarUtils;
import ai.chat2db.spi.ISQLIdentifierProcessor;
import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.spi.DefaultSQLExecutor;
import ai.chat2db.spi.util.SortUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ai.chat2db.plugin.oscar.constant.OscarMetaDataConstants.SYSTEM_SCHEMAS;

public abstract class OscarBaseMetaData extends DefaultMetaService {

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        List<Schema> schemas = DefaultSQLExecutor.getInstance().schemas(connection, databaseName, null);
        return SortUtils.sortSchema(schemas, SYSTEM_SCHEMAS);
    }

    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names)
                .filter(StringUtils::isNotBlank)
                .map(this::quoteIdentifierIgnoreCase)
                .collect(Collectors.joining(SQLConstants.DOT));
    }

    @Override
    public List<String> getSystemSchemas() {
        return SYSTEM_SCHEMAS;
    }

    @Override
    public ISQLIdentifierProcessor getSQLIdentifierProcessor() {
        return OscarUtils.OSCAR_SQL_IDENTIFIER_PROCESSOR;
    }

    @Override
    public Table getTable(List<Table> tables, String tableName) {
        if (StringUtils.isBlank(tableName) || CollectionUtils.isEmpty(tables)) {
            return null;
        }
        String normalizedTableName = normalizeIdentifier(tableName);
        for (Table table : tables) {
            if (StringUtils.equalsIgnoreCase(table.getName(), normalizedTableName)) {
                return table;
            }
        }
        return null;
    }

    @Override
    public Boolean supportCrossSchema() {
        return Boolean.TRUE;
    }

    protected String normalizeSchema(String schemaName) {
        return OscarUtils.normalizeSchema(schemaName);
    }

    protected String normalizeIdentifier(String identifier) {
        return OscarUtils.normalizeIdentifier(identifier);
    }

    protected String quoteIdentifierIgnoreCase(String identifier) {
        return OscarUtils.quoteIdentifierIgnoreCase(identifier);
    }

    protected String qualifiedName(String schemaName, String objectName) {
        return OscarUtils.qualifiedName(schemaName, objectName);
    }
}
