package ai.chat2db.plugin.hive.builder;

import ai.chat2db.spi.constant.SQLConstants;

import ai.chat2db.plugin.hive.enums.type.HiveColumnTypeEnum;
import ai.chat2db.plugin.hive.enums.type.HiveIndexTypeEnum;
import ai.chat2db.spi.DefaultSqlBuilder;
import ai.chat2db.spi.model.request.PageLimitRequest;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Table;
import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.community.domain.api.config.TableBuilderConfig;
import org.apache.commons.lang3.StringUtils;


import static ai.chat2db.plugin.hive.constant.HiveSqlBuilderConstants.*;
public class HiveSqlBuilder extends DefaultSqlBuilder {









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
            HiveColumnTypeEnum typeEnum = HiveColumnTypeEnum.getByType(column.getColumnType());
            if(typeEnum == null){
                continue;
            }
            script.append(SQLConstants.TAB).append(typeEnum.buildCreateColumnSql(column)).append(SQLConstants.COMMA_LINE_SEPARATOR);
        }
        for (TableIndex tableIndex : table.getIndexList()) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            HiveIndexTypeEnum hiveIndexTypeEnum = HiveIndexTypeEnum.getByType(tableIndex.getType());
            if(hiveIndexTypeEnum == null){
                continue;
            }
            script.append(SQLConstants.TAB).append(SQLConstants.EMPTY).append(hiveIndexTypeEnum.buildIndexScript(tableIndex)).append(SQLConstants.COMMA_LINE_SEPARATOR);
        }

        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append(SQLConstants.LINE_SEPARATOR_CLOSE_PARENTHESIS);


        if (StringUtils.isNotBlank(table.getEngine())) {
            script.append(SQLConstants.ENGINE_SQL).append(table.getEngine());
        }

        if (StringUtils.isNotBlank(table.getCharset())) {
            script.append(SQLConstants.DEFAULT_CHARACTER_SET_SQL).append(table.getCharset());
        }

        if (StringUtils.isNotBlank(table.getCollate())) {
            script.append(SQLConstants.COLLATE_SQL).append(table.getCollate());
        }

        if (table.getIncrementValue() != null) {
            script.append(SQLConstants.AUTO_INCREMENT_SQL).append(table.getIncrementValue());
        }

        if (StringUtils.isNotBlank(table.getComment())) {
            script.append(SQL_COMMENT).append(table.getComment()).append(SQLConstants.SINGLE_QUOTE);
        }

        if (StringUtils.isNotBlank(table.getPartition())) {
            script.append(VALUE_LOCAL_SQL_PART).append(table.getPartition());
        }
        script.append(SQLConstants.SEMICOLON);

        return script.toString();
    }

    @Override
    public String buildAlterTable(Table oldTable, Table newTable) {
        StringBuilder script = new StringBuilder();
        boolean isModify = false;
        script.append(SQL_ALTER_TABLE);
        if (StringUtils.isNotBlank(newTable.getDatabaseName())) {
            script.append(SQLConstants.BACK_QUOTE).append(newTable.getDatabaseName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.DOT);
        }
        script.append(SQLConstants.BACK_QUOTE).append(oldTable.getName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.LINE_SEPARATOR);
        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append(SQLConstants.TAB).append(SQL_RENAME).append(SQLConstants.BACK_QUOTE).append(newTable.getName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.SEMICOLON_LINE_SEPARATOR);
            isModify = true;
        }
        if (!StringUtils.equalsIgnoreCase(oldTable.getComment(), newTable.getComment())) {
            if (isModify) {
                script.append(SQL_ALTER_TABLE);
                if (StringUtils.isNotBlank(newTable.getDatabaseName())) {
                    script.append(SQLConstants.BACK_QUOTE).append(newTable.getDatabaseName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.DOT);
                }
                script.append(SQLConstants.BACK_QUOTE).append(newTable.getName()).append(SQLConstants.BACK_QUOTE).append(SQLConstants.LINE_SEPARATOR);
            }
            script.append(SQLConstants.TAB).append(SQL_SET_TBLPROPERTIES_COMMENT).append(SQLConstants.SINGLE_QUOTE).append(newTable.getComment()).append(VALUE_SINGLE_QUOTE_CLOSE_PAREN_COMMA);
        }
        for (TableColumn tableColumn : newTable.getColumnList()) {
            if (StringUtils.isNotBlank(tableColumn.getEditStatus()) && StringUtils.isNotBlank(tableColumn.getColumnType()) && StringUtils.isNotBlank(tableColumn.getName())) {
                HiveColumnTypeEnum typeEnum = HiveColumnTypeEnum.getByType(tableColumn.getColumnType());
                if(typeEnum == null){
                    continue;
                }
                script.append(SQLConstants.TAB).append(typeEnum.buildModifyColumn(tableColumn)).append(SQLConstants.COMMA_LINE_SEPARATOR);
            }
        }
        for (TableIndex tableIndex : newTable.getIndexList()) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus()) && StringUtils.isNotBlank(tableIndex.getType())) {
                HiveIndexTypeEnum hiveIndexTypeEnum = HiveIndexTypeEnum.getByType(tableIndex.getType());
                if(hiveIndexTypeEnum == null){
                    continue;
                }
                script.append(SQLConstants.TAB).append(hiveIndexTypeEnum.buildModifyIndex(tableIndex)).append(SQLConstants.COMMA_LINE_SEPARATOR);
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
        if (StringUtils.isNotBlank(database.getComment())) {
            sqlBuilder.append(SQL_COMMENT_SINGLE_QUOTE).append(database.getComment()).append(SQLConstants.SINGLE_QUOTE);

        }
        return sqlBuilder.toString();
    }

}
