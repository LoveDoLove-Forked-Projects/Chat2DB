package ai.chat2db.plugin.oscar.builder;

import ai.chat2db.plugin.oscar.util.OscarUtils;
import ai.chat2db.community.tools.util.EasyStringUtils;
import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.community.domain.api.model.view.ModifyView;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public abstract class OscarBaseSqlBuilder extends DefaultSqlBuilder {

    @Override
    public String buildAITableSchema(Table table) {
        return buildCreateTable(table, TableBuilderConfig.defaultConfig());
    }

    @Override
    public String buildCreateSchema(Schema schema) {
        StringBuilder sqlBuilder = new StringBuilder(SQLConstants.CREATE_SCHEMA_SQL_PREFIX)
                .append(quoteIdentifierIgnoreCase(schema.getName()));
        if (StringUtils.isNotBlank(schema.getOwner())) {
            sqlBuilder.append(SQLConstants.SCHEMA_AUTHORIZATION_SQL)
                    .append(quoteIdentifierIgnoreCase(schema.getOwner()));
        }
        return sqlBuilder.toString();
    }

    @Override
    protected void buildTableName(String databaseName, String schemaName, String tableName, StringBuilder script) {
        if (StringUtils.isNotBlank(schemaName)) {
            script.append(quoteIdentifierIgnoreCase(schemaName)).append(SQLConstants.DOT);
        }
        script.append(quoteIdentifierIgnoreCase(tableName));
    }

    @Override
    protected void buildColumns(List<String> columnList, StringBuilder script) {
        if (CollectionUtils.isNotEmpty(columnList)) {
            script.append(SQLConstants.SPACE).append(SQLConstants.OPEN_PARENTHESIS)
                    .append(columnList.stream()
                            .map(this::quoteIdentifierIgnoreCase)
                            .collect(Collectors.joining(SQLConstants.COMMA)))
                    .append(SQLConstants.CLOSE_PARENTHESIS)
                    .append(SQLConstants.SPACE);
        }
    }

    @Override
    public String buildCreateView(ModifyView modifyView) {
        StringBuilder sqlBuilder = new StringBuilder(SQLConstants.CREATE_SQL_PREFIX);
        if (modifyView.isUseOrReplace()) {
            sqlBuilder.append(SQLConstants.SQL_OR_REPLACE);
        }
        sqlBuilder.append(SQLConstants.CREATE_VIEW_KEYWORD);
        if (StringUtils.isNotBlank(modifyView.getSchemaName())) {
            sqlBuilder.append(quoteIdentifierIgnoreCase(modifyView.getSchemaName()))
                    .append(SQLConstants.DOT);
        }
        sqlBuilder.append(quoteIdentifierIgnoreCase(modifyView.getViewName()))
                .append(SQLConstants.SQL_AS_LINE_SEPARATOR)
                .append(StringUtils.defaultString(modifyView.getViewBody()).trim());
        return sqlBuilder.toString();
    }

    protected String buildTableComment(Table table) {
        return SQLConstants.COMMENT_ON_TABLE_SQL_PREFIX + qualifiedName(table.getSchemaName(), table.getName())
                + SQLConstants.SQL_IS + EasyStringUtils.escapeAndQuoteString(table.getComment());
    }

    protected String buildComment(TableColumn column) {
        return SQLConstants.COMMENT_ON_COLUMN_SQL_PREFIX
                + qualifiedName(column.getSchemaName(), column.getTableName())
                + SQLConstants.DOT
                + quoteIdentifierIgnoreCase(column.getName())
                + SQLConstants.SQL_IS
                + EasyStringUtils.escapeAndQuoteString(column.getComment());
    }

    protected String qualifiedName(String schemaName, String objectName) {
        return OscarUtils.qualifiedName(schemaName, objectName);
    }

    protected String quoteIdentifierIgnoreCase(String identifier) {
        return OscarUtils.quoteIdentifierIgnoreCase(identifier);
    }
}
