package ai.chat2db.plugin.postgresql.parser.listener;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.model.parser.info.ColumnInfo;
import ai.chat2db.community.domain.api.model.parser.info.TableInfo;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParser;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParserBaseListener;
import ai.chat2db.spi.util.SqlStringUtil;
import ai.chat2db.spi.util.TokenUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class PgsqlSelectListener extends PostgreSQLParserBaseListener {

    private final StatementContext context;

    private Map<TableInfo, String> tableAliasMap = new HashMap<>();

    private Map<ColumnInfo, String> columnAliasMap = new HashMap<>();


    public PgsqlSelectListener(StatementContext context) {
        this.context = context;
    }

    public void parserSelectStatement(ParseTree parseTree) {
        tableAliasMap = new HashMap<>();
        columnAliasMap = new HashMap<>();
        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        parseTreeWalker.walk(this, parseTree);
    }


    @Override
    public void exitSelectstmt(PostgreSQLParser.SelectstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        currentStatement.setColumnAliasMap(columnAliasMap);
        currentStatement.setTableAliasMap(tableAliasMap);
    }

    @Override
    public void enterTable_ref(PostgreSQLParser.Table_refContext ctx) {
        visitTable_refContext(ctx);
        List<PostgreSQLParser.Table_refContext> tableRefContexts = ctx.table_ref();
        for (PostgreSQLParser.Table_refContext table_refContext : tableRefContexts) {
            visitTable_refContext(table_refContext);
        }

    }

    private void visitTable_refContext(PostgreSQLParser.Table_refContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        if (Objects.isNull(ctx)) {
            return;
        }
        boolean isFunctionTable = false;
        PostgreSQLParser.Func_tableContext funcTableContext = ctx.func_table();
        if (Objects.nonNull(funcTableContext)) {
            isFunctionTable = true;
        }
        PostgreSQLParser.Relation_exprContext relationExprContext = ctx.relation_expr();
        if (!isFunctionTable && Objects.isNull(relationExprContext)) {
            return;
        }
        PostgreSQLParser.Opt_alias_clauseContext optAliasClauseContext = ctx.opt_alias_clause();
        CommonTokenStream commonTokenStream = context.getCommonTokenStream();
        List<Token> tokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(commonTokenStream, isFunctionTable ? funcTableContext : relationExprContext);
        Token database = null, schema = null, table = null;
        String databaseText = null, schemaText = null, tableText = null;
        TableInfo tableInfo;
        String alias = null;
        int size = tokensOnDefault.size();
        if (size == 5) {
            database = tokensOnDefault.get(0);
            schema = tokensOnDefault.get(2);
            table = tokensOnDefault.get(4);
        } else if (size == 3) {
            schema = tokensOnDefault.get(0);
            table = tokensOnDefault.get(2);
        } else {
            table = tokensOnDefault.get(0);
        }
        if (Objects.nonNull(database)) {
            databaseText = SqlStringUtil.removeQuote(database.getText());
        }
        if (Objects.nonNull(schema)) {
            schemaText = SqlStringUtil.removeQuote(schema.getText());
        }
        if (Objects.nonNull(table) && !isFunctionTable) {
            tableText = table.getText();
            if (!SqlStringUtil.isQuote(tableText)) {
                tableText = tableText.toLowerCase();
            }
            tableInfo = TableInfo.builder().database(databaseText).schema(schemaText).table(tableText).build();
            if (Objects.nonNull(optAliasClauseContext)) {
                PostgreSQLParser.Table_alias_clauseContext tableAliasClauseContext = optAliasClauseContext.table_alias_clause();
                if (Objects.nonNull(tableAliasClauseContext)) {
                    PostgreSQLParser.Table_aliasContext tableAliasContext = tableAliasClauseContext.table_alias();
                    if (Objects.nonNull(tableAliasContext)) {
                        alias = tableAliasContext.getText();
                    }
                }
            }
            tableAliasMap.put(tableInfo, alias);
        }
    }

    private Identifier splitColumName(CommonTokenStream commonTokenStream, ParseTree child) {
        try {
            Token database = null, schema = null, table = null, column = null;
            String databaseText = null, schemaText = null, tableText = null, columnText = null;
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            List<Token> tokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(commonTokenStream, child);
            int size = tokensOnDefault.size();
            if (size == 7) {
                database = tokensOnDefault.get(0);
                schema = tokensOnDefault.get(2);
                table = tokensOnDefault.get(4);
                databaseText = SqlStringUtil.removeQuote(database.getText());
                schemaText = SqlStringUtil.removeQuote(schema.getText());
                tableText = SqlStringUtil.removeQuote(table.getText());
            } else if (size == 5) {
                schema = tokensOnDefault.get(0);
                table = tokensOnDefault.get(2);
                schemaText = SqlStringUtil.removeQuote(schema.getText());
                tableText = SqlStringUtil.removeQuote(table.getText());
            } else if (size == 3) {
                table = tokensOnDefault.get(0);
                tableText = SqlStringUtil.removeQuote(table.getText());
            }
            column = tokensOnDefault.get(size - 1);
            columnText = SqlStringUtil.removeQuote(column.getText());
            if (Objects.nonNull(database)) {
                currentStatement.addIdentifier(databaseText, IdentifierTypeEnum.DATABASE.name(), database);
            }

            if (Objects.nonNull(schema)) {
                Identifier identifier = new Identifier();
                identifier.setIdentifierName(schemaText);
                identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                identifier.setFirstToken(schema);
                identifier.setIdentifierDatabase(databaseText);
                currentStatement.addIdentifier(identifier);
            }

            if (Objects.nonNull(table)) {
                Identifier identifier = new Identifier();
                identifier.setIdentifierName(tableText);
                identifier.setIdentifierType(IdentifierTypeEnum.TABLE.name());
                identifier.setFirstToken(table);
                identifier.setIdentifierDatabase(databaseText);
                identifier.setIdentifierSchema(schemaText);
                currentStatement.addIdentifier(identifier);
            }

            Identifier identifier = new Identifier();
            identifier.setIdentifierName(columnText);
            identifier.setIdentifierType(IdentifierTypeEnum.COLUMN.name());
            identifier.setFirstToken(column);
            identifier.setIdentifierDatabase(databaseText);
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierTable(tableText);
            return identifier;
        } catch (Exception e) {
            log.error("pgsql splitColumName error ", e);
        }
        return null;
    }


    @Override
    public void enterSimple_select_pramary(PostgreSQLParser.Simple_select_pramaryContext ctx) {
        Try.of(() -> {
            PostgreSQLParser.From_clauseContext fromClauseContext = ctx.from_clause();
            if (Objects.isNull(fromClauseContext) || fromClauseContext.getChildCount() <= 0) {
                return null;
            }
            PostgreSQLParser.Opt_target_listContext optTargetListContext = ctx.opt_target_list();
            if (Objects.isNull(optTargetListContext)) {
                return null;
            }
            PostgreSQLParser.Target_listContext targetListContext = optTargetListContext.target_list();
            if (Objects.isNull(targetListContext)) {
                return null;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            CommonTokenStream commonTokenStream = context.getCommonTokenStream();
            for (PostgreSQLParser.Target_elContext targetElContext : targetListContext.target_el()) {

                int childCount = targetElContext.getChildCount();
                if (childCount < 1) {
                    continue;
                }
                Token alias = null;
                String aliasText = null;
                if (childCount == 2 || childCount == 3) {
                    alias = targetElContext.getStop();
                    aliasText = SqlStringUtil.removeQuote(alias.getText());
                }
                ParseTree child = targetElContext.getChild(0);

                Identifier identifier = splitColumName(commonTokenStream, child);

                if (identifier == null) {
                    return null;
                }
                if (Objects.nonNull(alias)) {
                    identifier.setIdentifierAlias(aliasText);
                    identifier.setLastToken(alias);
                }
                columnAliasMap.put(
                        ColumnInfo.builder()
                                .database(identifier.getIdentifierDatabase())
                                .schema(identifier.getIdentifierSchema())
                                .table(identifier.getIdentifierTable())
                                .column(identifier.getIdentifierName())
                                .build(),
                        identifier.getIdentifierAlias());
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitSimple_select_pramary error", e));

    }

}
