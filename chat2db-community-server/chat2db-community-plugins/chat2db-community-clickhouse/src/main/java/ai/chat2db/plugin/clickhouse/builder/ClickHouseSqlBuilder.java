package ai.chat2db.plugin.clickhouse.builder;

import ai.chat2db.spi.constant.SQLConstants;

import ai.chat2db.plugin.clickhouse.enums.type.ClickHouseColumnTypeEnum;
import ai.chat2db.plugin.clickhouse.enums.type.ClickHouseIndexTypeEnum;
import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.spi.model.request.PageLimitRequest;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import org.apache.commons.lang3.StringUtils;


import static ai.chat2db.plugin.clickhouse.constant.ClickHouseSqlBuilderConstants.*;
public class ClickHouseSqlBuilder extends DefaultSqlBuilder {






    @Override
    public String buildCreateTable(Table table, TableBuilderConfig tableBuilderConfig) {
        StringBuilder script = new StringBuilder();
        script.append(SQL_CREATE_TABLE);
        if (StringUtils.isNotBlank(table.getDatabaseName())) {
            script.append(SQLConstants.BACK_QUOTE).append(table.getDatabaseName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.DOT);
        }
        script.append(SQLConstants.BACK_QUOTE).append(table.getName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.SPACE_OPEN_PARENTHESIS).append(SQLConstants.LINE_SEPARATOR);
        for (TableColumn column : table.getColumnList()) {
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(column.getColumnType())) {
                continue;
            }
            ClickHouseColumnTypeEnum typeEnum = ClickHouseColumnTypeEnum.getByType(column.getColumnType());
            if (typeEnum == null){
                script.append(SQLConstants.TAB).append(buildDefaultCreateColumnSql(column)).append(SQLConstants.COMMA_LINE_SEPARATOR);
            }else {
                script.append(SQLConstants.TAB).append(typeEnum.buildCreateColumnSql(column)).append(SQLConstants.COMMA_LINE_SEPARATOR);
            }
        }
        for (TableIndex tableIndex : table.getIndexList()) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            ClickHouseIndexTypeEnum mysqlIndexTypeEnum = ClickHouseIndexTypeEnum.getByType(tableIndex.getType());
            if (!ClickHouseIndexTypeEnum.PRIMARY.equals(mysqlIndexTypeEnum) ) {
                script.append(SQLConstants.TAB).append(SQLConstants.EMPTY).append(mysqlIndexTypeEnum.buildIndexScript(tableIndex)).append(SQLConstants.COMMA_LINE_SEPARATOR);
            }
        }

        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append(SQLConstants.LINE_SEPARATOR_CLOSE_PARENTHESIS);


        if (StringUtils.isNotBlank(table.getEngine())) {
            script.append(SQLConstants.ENGINE_SQL).append(table.getEngine()).append(SQLConstants.LINE_SEPARATOR);
        }
        for (TableIndex tableIndex : table.getIndexList()) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            ClickHouseIndexTypeEnum mysqlIndexTypeEnum = ClickHouseIndexTypeEnum.getByType(tableIndex.getType());
            if (ClickHouseIndexTypeEnum.PRIMARY.equals(mysqlIndexTypeEnum) ) {
                script.append(SQLConstants.TAB).append(SQLConstants.EMPTY).append(mysqlIndexTypeEnum.buildIndexScript(tableIndex)).append(SQLConstants.LINE_SEPARATOR);
            }
        }

        if (StringUtils.isNotBlank(table.getComment())) {
            script.append(SQL_COMMENT).append(table.getComment()).append(SQLConstants.SINGLE_QUOTE);
        }

        script.append(SQLConstants.SEMICOLON);

        return script.toString();
    }

    @Override
    public String buildAlterTable(Table oldTable, Table newTable) {
        StringBuilder script = new StringBuilder();
        script.append(SQL_ALTER_TABLE);
        if (StringUtils.isNotBlank(oldTable.getDatabaseName())) {
            script.append(SQLConstants.BACK_QUOTE).append(oldTable.getDatabaseName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.DOT);
        }
        script.append(SQLConstants.BACK_QUOTE).append(oldTable.getName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.LINE_SEPARATOR);

        if (!StringUtils.equalsIgnoreCase(oldTable.getComment(), newTable.getComment())) {
            script.append(SQLConstants.TAB).append(SQL_MODIFY_COMMENT).append(SQLConstants.SINGLE_QUOTE).append(newTable.getComment()).append(SQLConstants.SINGLE_QUOTE).append(SQLConstants.COMMA_LINE_SEPARATOR);
        }
        for (TableColumn tableColumn : newTable.getColumnList()) {
            if (StringUtils.isNotBlank(tableColumn.getEditStatus()) && StringUtils.isNotBlank(tableColumn.getColumnType()) && StringUtils.isNotBlank(tableColumn.getName())) {
                ClickHouseColumnTypeEnum typeEnum = ClickHouseColumnTypeEnum.getByType(tableColumn.getColumnType());
                if(typeEnum == null){
                    continue;
                }
                script.append(SQLConstants.TAB).append(typeEnum.buildModifyColumn(tableColumn)).append(SQLConstants.COMMA_LINE_SEPARATOR);
            }
        }
        for (TableIndex tableIndex : newTable.getIndexList()) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus()) && StringUtils.isNotBlank(tableIndex.getType())) {
                ClickHouseIndexTypeEnum clickHouseIndexTypeEnum = ClickHouseIndexTypeEnum
                        .getByType(tableIndex.getType());
                if(clickHouseIndexTypeEnum == null){
                    continue;
                }
                script.append(SQLConstants.TAB).append(clickHouseIndexTypeEnum.buildModifyIndex(tableIndex)).append(SQLConstants.COMMA_LINE_SEPARATOR);
            }
        }

        if (script.length() > 2) {
            script = new StringBuilder(script.substring(0, script.length() - 2));
            script.append(SQLConstants.SEMICOLON);
        }

        return script.toString();
    }


    @Override
    public String buildPageLimit(PageLimitRequest request) {
        String sql = request.getSql();
        int offset = request.getOffset();
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (offset == 0) {
            sqlBuilder.append(SQLConstants.LINE_SEPARATOR_LIMIT_SQL);
            sqlBuilder.append(pageSize);
        } else {
            sqlBuilder.append(SQLConstants.LINE_SEPARATOR_LIMIT_SQL);
            sqlBuilder.append(offset);
            sqlBuilder.append(SQLConstants.COMMA);
            sqlBuilder.append(pageSize);
        }
        return sqlBuilder.toString();
    }


    @Override
    public String buildCreateDatabase(Database database) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(SQL_CREATE_DATABASE + database.getName() + SQLConstants.BACK_QUOTE);
        if(StringUtils.isNotBlank(database.getComment())){
            sqlBuilder.append(SQL_SEMICOLON_ALTER_DATABASE).append(database.getName()).append(SQL_COMMENT).append(database.getComment()).append(SQLConstants.SINGLE_QUOTE_SEMICOLON);
        }
        return sqlBuilder.toString();
    }

}
