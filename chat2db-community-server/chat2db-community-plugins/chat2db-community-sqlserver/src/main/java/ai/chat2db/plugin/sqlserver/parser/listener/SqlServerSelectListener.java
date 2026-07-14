package ai.chat2db.plugin.sqlserver.parser.listener;

import ai.chat2db.community.domain.api.model.parser.info.ColumnInfo;
import ai.chat2db.community.domain.api.model.parser.info.TableInfo;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParser;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParserBaseListener;
import ai.chat2db.plugin.sqlserver.parser.util.SqlServerStringUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SqlServerSelectListener extends TSqlParserBaseListener {

    private final StatementContext context;

    private Map<TableInfo, String> tableAliasMap = new HashMap<>();

    private Map<ColumnInfo, String> columnAliasMap = new HashMap<>();


    public SqlServerSelectListener(StatementContext context) {
        this.context = context;
    }

    public void parserSelectStatement(ParseTree parseTree) {
        tableAliasMap = new HashMap<>();
        columnAliasMap = new HashMap<>();
        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        parseTreeWalker.walk(this, parseTree);
    }

    @Override
    public void exitSelect_statement_standalone(TSqlParser.Select_statement_standaloneContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setColumnAliasMap(columnAliasMap);
        currentStatement.setTableAliasMap(tableAliasMap);
    }

    @Override
    public void enterTable_source_item(TSqlParser.Table_source_itemContext ctx) {
        TSqlParser.Full_table_nameContext fullTableNameContext = ctx.full_table_name();
        TSqlParser.As_table_aliasContext tableAlias = ctx.as_table_alias();
        if (Objects.isNull(fullTableNameContext)) {
            return;
        }
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        Try.run(() -> {
            String database = null, schema = null, table = null, alias = null;
            String text = fullTableNameContext.getText();
            if (StringUtils.isNotBlank(text)) {
                String[] split = text.split("\\.");
                if (split.length == 3) {
                    database = SqlServerStringUtil.removeQuote(split[0]);
                    schema = SqlServerStringUtil.removeQuote(split[1]);
                } else if (split.length == 2) {
                    schema = SqlServerStringUtil.removeQuote(split[0]);
                }
                table = SqlServerStringUtil.removeQuote(split[split.length - 1]);
                if (Objects.nonNull(tableAlias)) {
                    TSqlParser.Table_aliasContext tableAliasContext = tableAlias.table_alias();
                    if (Objects.nonNull(tableAliasContext)) {
                        alias = tableAliasContext.getText();
                    }
                }
                TableInfo tableInfo = TableInfo.builder().database(database).schema(schema).table(table).build();
                tableAliasMap.put(tableInfo, alias);
            }
        }).onFailure(e -> log.error("enter table_source_item error", e));
    }

    @Override
    public void enterSelect_list_elem(TSqlParser.Select_list_elemContext ctx) {
        TSqlParser.AsteriskContext asterisk = ctx.asterisk();
        if (asterisk != null) {
            TSqlParser.Table_nameContext tableNameContext = asterisk.table_name();
            String database = null, schema = null, table = null;
            if (tableNameContext != null) {
                String tableName = tableNameContext.getText();
                String[] split = tableName.split("\\.");
                if (split.length == 3) {
                    database = SqlServerStringUtil.removeQuote(split[0]);
                    schema = SqlServerStringUtil.removeQuote(split[1]);
                } else if (split.length == 2) {
                    schema = SqlServerStringUtil.removeQuote(split[0]);
                }
                table = SqlServerStringUtil.removeQuote(split[split.length - 1]);
            }
            columnAliasMap.put(
                    ColumnInfo.builder()
                            .database(database)
                            .schema(schema)
                            .table(table)
                            .column("*")
                            .build(), null
            );
            return;
        }

        TSqlParser.Expression_elemContext expressionElemContext = ctx.expression_elem();
        if (Objects.isNull(expressionElemContext)) {
            return;
        }
        TSqlParser.ExpressionContext expression = expressionElemContext.expression();
        if (Objects.isNull(expression)) {
            return;
        }
        ColumnInfo columnInfo;
        String database = null, schema = null, table = null, column = null, alias = null;
        ParseTree child = expression.getChild(0);
        String text = child.getText();
        String[] split = text.split("\\.");
        int length = split.length;
        if (length == 4) {
            database = SqlServerStringUtil.removeQuote(split[0]);
            schema = SqlServerStringUtil.removeQuote(split[1]);
            table = SqlServerStringUtil.removeQuote(split[2]);
        } else if (length == 3) {
            schema = SqlServerStringUtil.removeQuote(split[0]);
            table = SqlServerStringUtil.removeQuote(split[1]);
        } else if (length == 2) {
            table = SqlServerStringUtil.removeQuote(split[0]);
        }
        column = SqlServerStringUtil.removeQuote(split[length - 1]);
        TSqlParser.As_column_aliasContext columnAlias = expressionElemContext.as_column_alias();
        if (Objects.nonNull(columnAlias)) {
            TSqlParser.Column_aliasContext columnAliasContext = columnAlias.column_alias();
            if (Objects.nonNull(columnAliasContext)) {
                alias = columnAliasContext.getText();
            }

        }
        columnInfo = ColumnInfo.builder().database(database).schema(schema).table(table).column(column).build();
        columnAliasMap.put(columnInfo, alias);
    }
}
