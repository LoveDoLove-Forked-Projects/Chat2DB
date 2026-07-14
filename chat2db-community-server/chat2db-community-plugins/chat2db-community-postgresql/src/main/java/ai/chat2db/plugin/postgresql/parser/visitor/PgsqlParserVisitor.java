package ai.chat2db.plugin.postgresql.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertRowTokenRange;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParser;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParserBaseVisitor;
import ai.chat2db.spi.util.InsertValueTokenUtil;
import ai.chat2db.spi.util.SqlCommentUtil;
import ai.chat2db.spi.util.SqlStringUtil;
import ai.chat2db.spi.util.TokenUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
public class PgsqlParserVisitor extends PostgreSQLParserBaseVisitor<Void> {


    private final StatementContext context;

    public PgsqlParserVisitor(StatementContext context) {
        this.context = context;
    }


    @Override
    public Void visitStmtmulti(PostgreSQLParser.StmtmultiContext ctx) {
        List<PostgreSQLParser.StmtContext> stmt = ctx.stmt();
        TokenStream commonTokenStream = context.getCommonTokenStream();
        for (int i = 0; i < stmt.size(); i++) {
            PostgreSQLParser.StmtContext child = stmt.get(i);
            Token start = child.getStart();
            Token stop = child.getStop();
            if (start.getType() == PostgreSQLLexer.MetaCommand) {
                continue;
            }
            Statement statement = new Statement();
            int sqlCommentSearchStartIndex = 0;
            if (i != 0) {
                PostgreSQLParser.StmtContext stmtContext = stmt.get(i - 1);
                if (Objects.nonNull(stmtContext)) {
                    sqlCommentSearchStartIndex = stmtContext.getStop().getTokenIndex();

                }
            }
            int sqlCommentSearchEndIndex = start.getTokenIndex();
            String sqlComment = SqlCommentUtil.searchSqlComment(sqlCommentSearchStartIndex, sqlCommentSearchEndIndex, commonTokenStream);
            statement.setComment(sqlComment);
            String sql = "";
            if (StringUtils.isNotBlank(sqlComment)) {
                sql = "--" + " " + sqlComment + "\n";
            }
            sql += commonTokenStream.getText(child.getSourceInterval());
            statement.setSql(sql);
            statement.setStatementType(StatementValidTypeEnum.VALID.name());
            statement.setFirstToken(start);
            statement.setLastToken(stop);
            context.setCurrentStatement(statement);
            context.addStatement(statement);
            visit(child);
        }
        return null;
    }


    private void visitRoleOrUser(PostgreSQLParser.RoleidContext ctx, IdentifierTypeEnum identifierTypeEnum) {
        if (Objects.nonNull(ctx)) {
            Token start = ctx.getStart();
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            currentStatement.addIdentifier(start.getText(), identifierTypeEnum.name(), start);
        }

    }

    @Override
    public Void visitAltertablestmt(PostgreSQLParser.AltertablestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_TABLE.name());
        PostgreSQLParser.Relation_exprContext relationExprContext = ctx.relation_expr();
        splitName(IdentifierTypeEnum.TABLE, relationExprContext);
        return super.visitAltertablestmt(ctx);
    }

    @Override
    public Void visitCreaterolestmt(PostgreSQLParser.CreaterolestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ROLE.name());
        PostgreSQLParser.RoleidContext roleid = ctx.roleid();
        visitRoleOrUser(roleid, IdentifierTypeEnum.ROLE);
        return super.visitCreaterolestmt(ctx);
    }


    @Override
    public Void visitCreateuserstmt(PostgreSQLParser.CreateuserstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        PostgreSQLParser.RoleidContext roleid = ctx.roleid();
        visitRoleOrUser(roleid, IdentifierTypeEnum.USER);
        return super.visitCreateuserstmt(ctx);
    }


    @Override
    public Void visitCreateschemastmt(PostgreSQLParser.CreateschemastmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_SCHEMA.name());
        PostgreSQLParser.ColidContext colid = ctx.colid();
        if (Objects.nonNull(colid)) {
            Token start = colid.getStart();
            currentStatement.addIdentifier(start.getText(), IdentifierTypeEnum.SCHEMA.name(), start);
        }
        return super.visitCreateschemastmt(ctx);
    }

    @Override
    public Void visitVariablesetstmt(PostgreSQLParser.VariablesetstmtContext ctx) {
        Try.of(() -> {
            PostgreSQLParser.Set_restContext setRestContext = ctx.set_rest();
            if (Objects.isNull(setRestContext)) {
                return null;
            }
            PostgreSQLParser.Set_rest_moreContext setRestMoreContext = setRestContext.set_rest_more();
            if (Objects.isNull(setRestMoreContext)) {
                return null;
            }
            PostgreSQLParser.Generic_setContext genericSetContext = setRestMoreContext.generic_set();

            if (Objects.isNull(genericSetContext)) {
                return null;
            }
            PostgreSQLParser.Var_nameContext varNameContext = genericSetContext.var_name();
            if (Objects.isNull(varNameContext)) {
                return null;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            for (PostgreSQLParser.ColidContext colidContext : varNameContext.colid()) {
                Token start = colidContext.getStart();
                if ("search_path".equalsIgnoreCase(start.getText())) {
                    PostgreSQLParser.Var_listContext varListContext = genericSetContext.var_list();
                    if (Objects.nonNull(varListContext)) {

                        currentStatement.setType(SqlTypeEnum.SET_SCHEMA.name());
                        for (PostgreSQLParser.Var_valueContext varValueContext : varListContext.var_value()) {
                            Token value = varValueContext.getStart();
                            currentStatement.addIdentifier(value.getText(), IdentifierTypeEnum.SCHEMA.name(), value);
                        }

                    }
                }
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitVariablesetstmt error", e));

        return super.visitVariablesetstmt(ctx);
    }


    @Override
    public Void visitInsertstmt(PostgreSQLParser.InsertstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        visitInsertValueMappings(context, ctx);
        return super.visitInsertstmt(ctx);
    }

    static void visitInsertValueMappings(StatementContext context, PostgreSQLParser.InsertstmtContext ctx) {
        Try.run(() -> {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement) || Objects.isNull(ctx)) {
                return;
            }
            PostgreSQLParser.Insert_restContext insertRestContext = ctx.insert_rest();
            if (Objects.isNull(insertRestContext)) {
                return;
            }
            PostgreSQLParser.Insert_column_listContext columnListContext = insertRestContext.insert_column_list();
            PostgreSQLParser.Values_clauseContext valuesClauseContext = getInsertValuesClause(insertRestContext.selectstmt());
            if (Objects.isNull(columnListContext)) {
                return;
            }
            List<PostgreSQLParser.Insert_column_itemContext> columnContexts = columnListContext.insert_column_item();
            List<PostgreSQLParser.Expr_listContext> rowContexts = Objects.nonNull(valuesClauseContext)
                    ? valuesClauseContext.expr_list() : List.of();
            List<InsertRowTokenRange> rowTokenRanges = getInsertRowTokenRanges(context, ctx, valuesClauseContext);
            if (columnContexts.isEmpty() || rowTokenRanges.isEmpty()) {
                return;
            }
            int expressionRowIndex = 0;

            for (int rowIndex = 0; rowIndex < rowTokenRanges.size(); rowIndex++) {
                Token rowFirstToken = rowTokenRanges.get(rowIndex).getFirstToken();
                Token rowLastToken = rowTokenRanges.get(rowIndex).getLastToken();
                List<PostgreSQLParser.A_exprContext> valueContexts = List.of();
                if (rowContexts.size() > expressionRowIndex) {
                    PostgreSQLParser.Expr_listContext rowContext = rowContexts.get(expressionRowIndex);
                    if (Objects.nonNull(rowContext) && rowContext.getStart().getTokenIndex() > rowFirstToken.getTokenIndex()
                            && rowContext.getStop().getTokenIndex() < rowLastToken.getTokenIndex()) {
                        valueContexts = rowContext.a_expr();
                        expressionRowIndex++;
                    }
                }
                int columnSize = Math.min(columnContexts.size(), valueContexts.size());
                for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                    PostgreSQLParser.Insert_column_itemContext columnContext = columnContexts.get(columnIndex);
                    PostgreSQLParser.A_exprContext valueContext = valueContexts.get(columnIndex);
                    if (Objects.isNull(columnContext) || Objects.isNull(valueContext)) {
                        continue;
                    }
                    currentStatement.addInsertValueMapping(columnContext.getStart(), columnContext.getStop(),
                            valueContext.getStart(), valueContext.getStop(), rowFirstToken, rowLastToken,
                            rowIndex, columnIndex);
                }
                for (int columnIndex = columnSize; columnIndex < columnContexts.size(); columnIndex++) {
                    PostgreSQLParser.Insert_column_itemContext columnContext = columnContexts.get(columnIndex);
                    if (Objects.isNull(columnContext)) {
                        continue;
                    }
                    currentStatement.addUnmappedInsertColumn(columnContext.getStart(), columnContext.getStop(),
                            rowFirstToken, rowLastToken, rowIndex, columnIndex);
                }
                for (int valueIndex = columnSize; valueIndex < valueContexts.size(); valueIndex++) {
                    PostgreSQLParser.A_exprContext valueContext = valueContexts.get(valueIndex);
                    if (Objects.isNull(valueContext)) {
                        continue;
                    }
                    currentStatement.addUnmappedInsertValue(valueContext.getStart(), valueContext.getStop(),
                            rowFirstToken, rowLastToken, rowIndex, valueIndex);
                }
            }
        }).onFailure(e -> log.error("pgsql visitInsertValueMappings error", e));
    }

    private static List<InsertRowTokenRange> getInsertRowTokenRanges(StatementContext context,
                                                                     PostgreSQLParser.InsertstmtContext ctx,
                                                                     PostgreSQLParser.Values_clauseContext valuesClauseContext) {
        List<InsertRowTokenRange> rowTokenRanges = List.of();
        if (Objects.nonNull(valuesClauseContext)) {
            rowTokenRanges = InsertValueTokenUtil.buildRowTokenRanges(
                    valuesClauseContext.OPEN_PAREN(), valuesClauseContext.CLOSE_PAREN());
        }
        if (!rowTokenRanges.isEmpty()) {
            return rowTokenRanges;
        }
        return InsertValueTokenUtil.searchValuesRowTokenRanges(context, ctx.getStart(), ctx.getStop());
    }

    private static PostgreSQLParser.Values_clauseContext getInsertValuesClause(PostgreSQLParser.SelectstmtContext ctx) {
        if (Objects.isNull(ctx) || Objects.isNull(ctx.select_no_parens())) {
            return null;
        }
        PostgreSQLParser.Select_no_parensContext selectNoParensContext = ctx.select_no_parens();
        if (Objects.nonNull(selectNoParensContext.with_clause())) {
            return null;
        }
        PostgreSQLParser.Select_clauseContext selectClauseContext = selectNoParensContext.select_clause();
        if (Objects.isNull(selectClauseContext) || selectClauseContext.simple_select_intersect().size() != 1) {
            return null;
        }
        PostgreSQLParser.Simple_select_intersectContext simpleSelectIntersectContext =
                selectClauseContext.simple_select_intersect(0);
        if (Objects.isNull(simpleSelectIntersectContext)
                || simpleSelectIntersectContext.simple_select_pramary().size() != 1) {
            return null;
        }
        PostgreSQLParser.Simple_select_pramaryContext simpleSelectPramaryContext =
                simpleSelectIntersectContext.simple_select_pramary(0);
        if (Objects.isNull(simpleSelectPramaryContext)) {
            return null;
        }
        return simpleSelectPramaryContext.values_clause();
    }

    @Override
    public Void visitInsert_target(PostgreSQLParser.Insert_targetContext ctx) {
        splitName(IdentifierTypeEnum.TABLE, ctx);
        return super.visitInsert_target(ctx);
    }

    @Override
    public Void visitSelectstmt(PostgreSQLParser.SelectstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        return super.visitSelectstmt(ctx);
    }

    @Override
    public Void visitTable_ref(PostgreSQLParser.Table_refContext ctx) {
        visitTable_refContext(ctx);
        List<PostgreSQLParser.Table_refContext> tableRefContexts = ctx.table_ref();
        for (PostgreSQLParser.Table_refContext table_refContext : tableRefContexts) {
            visitTable_refContext(table_refContext);
        }
        return null;
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
            databaseText = database.getText();

            currentStatement.addIdentifier(databaseText, IdentifierTypeEnum.DATABASE.name(), database);
        }
        if (Objects.nonNull(schema)) {
            schemaText = schema.getText();
            Identifier identifier = new Identifier();
            identifier.setIdentifierDatabase(databaseText);
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            identifier.setFirstToken(schema);
            identifier.setIdentifierName(schemaText);
            currentStatement.addIdentifier(identifier);
        }
        if (Objects.nonNull(table)) {
            tableText = table.getText();
            Identifier identifier = new Identifier();
            identifier.setIdentifierDatabase(databaseText);
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierType(isFunctionTable ? IdentifierTypeEnum.FUNCTION.name() : IdentifierTypeEnum.TABLE.name());
            identifier.setFirstToken(table);
            identifier.setIdentifierName(tableText);
            if (Objects.nonNull(optAliasClauseContext)) {
                PostgreSQLParser.Table_alias_clauseContext tableAliasClauseContext = optAliasClauseContext.table_alias_clause();
                if (Objects.nonNull(tableAliasClauseContext)) {
                    PostgreSQLParser.Table_aliasContext tableAliasContext = tableAliasClauseContext.table_alias();
                    if (Objects.nonNull(tableAliasContext)) {
                        identifier.setIdentifierAlias(tableAliasContext.getText());
                        identifier.setLastToken(tableAliasContext.getStop());
                    }
                }
            }
            currentStatement.addIdentifier(identifier);
        }
    }

    @Override
    public Void visitUpdatestmt(PostgreSQLParser.UpdatestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UPDATE.name());

        Try.of(() -> {
            PostgreSQLParser.Relation_expr_opt_aliasContext relationExprOptAliasContext = ctx.relation_expr_opt_alias();
            if (Objects.nonNull(relationExprOptAliasContext)) {
                PostgreSQLParser.ColidContext colid = relationExprOptAliasContext.colid();
                if (Objects.isNull(colid)) {
                    visitTableName(relationExprOptAliasContext);
                } else {
                    PostgreSQLParser.Relation_exprContext relationExprContext = relationExprOptAliasContext.relation_expr();
                    if (Objects.isNull(relationExprContext)) {
                        return null;
                    }
                    Token database = null, schema = null, table = null, alias = colid.start;
                    String databaseText = null, schemaText = null, tableText = null, aliasText = SqlStringUtil.removeQuote(alias.getText());
                    CommonTokenStream commonTokenStream = context.getCommonTokenStream();
                    List<Token> tokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(commonTokenStream, relationExprContext);
                    int size = tokensOnDefault.size();
                    if (size == 5) {
                        database = tokensOnDefault.get(0);
                        schema = tokensOnDefault.get(2);
                        databaseText = SqlStringUtil.removeQuote(database.getText());
                        schemaText = SqlStringUtil.removeQuote(schema.getText());
                    } else if (size == 3) {
                        schema = tokensOnDefault.get(0);
                        schemaText = SqlStringUtil.removeQuote(schema.getText());
                    }
                    table = tokensOnDefault.get(size - 1);
                    tableText = SqlStringUtil.removeQuote(table.getText());
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

                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(tableText);
                    identifier.setIdentifierType(IdentifierTypeEnum.TABLE.name());
                    identifier.setFirstToken(table);
                    identifier.setIdentifierDatabase(databaseText);
                    identifier.setIdentifierSchema(schemaText);
                    identifier.setIdentifierAlias(aliasText);
                    identifier.setLastToken(alias);
                    currentStatement.addIdentifier(identifier);
                }
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitUpdatestmt error", e));
        return super.visitUpdatestmt(ctx);
    }

    @Override
    public Void visitSet_target(PostgreSQLParser.Set_targetContext ctx) {
        CommonTokenStream commonTokenStream = context.getCommonTokenStream();
        Identifier identifier = splitColumName(commonTokenStream, ctx);
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.addIdentifier(identifier);
        return null;
    }

    private void visitTableName(PostgreSQLParser.Relation_expr_opt_aliasContext ctx) {
        PostgreSQLParser.Relation_exprContext relationExprContext = ctx.relation_expr();
        if (Objects.nonNull(relationExprContext)) {
            splitName(IdentifierTypeEnum.TABLE, relationExprContext);
        }

    }

    @Override
    public Void visitDeletestmt(PostgreSQLParser.DeletestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        PostgreSQLParser.Relation_expr_opt_aliasContext relationExprOptAliasContext = ctx.relation_expr_opt_alias();
        if (Objects.nonNull(relationExprOptAliasContext)) {
            visitTableName(relationExprOptAliasContext);
        }

        return super.visitDeletestmt(ctx);
    }

    @Override
    public Void visitDropstmt(PostgreSQLParser.DropstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        PostgreSQLParser.Object_type_any_nameContext objectTypeAnyNameContext = ctx.object_type_any_name();
        if (Objects.nonNull(objectTypeAnyNameContext)) {
            String text = objectTypeAnyNameContext.getText();
            if (Objects.equals(IdentifierTypeEnum.TABLE.name(), text)) {
                currentStatement.setType(SqlTypeEnum.DROP_TABLE.name());
                PostgreSQLParser.Any_name_listContext anyNameListContext = ctx.any_name_list();
                if (Objects.nonNull(anyNameListContext)) {
                    for (PostgreSQLParser.Any_nameContext anyNameContext : anyNameListContext.any_name()) {
                        visitAnyName(anyNameContext, IdentifierTypeEnum.TABLE);
                    }
                }
            } else if (Objects.equals(IdentifierTypeEnum.VIEW.name(), text)) {

                currentStatement.setType(SqlTypeEnum.DROP_VIEW.name());
                PostgreSQLParser.Any_name_listContext anyNameListContext = ctx.any_name_list();
                if (Objects.nonNull(anyNameListContext)) {
                    for (PostgreSQLParser.Any_nameContext anyNameContext : anyNameListContext.any_name()) {
                        visitAnyName(anyNameContext, IdentifierTypeEnum.VIEW);
                    }
                }
            } else if (Objects.equals(IdentifierTypeEnum.TRIGGER.name(), text)) {

                currentStatement.setType(SqlTypeEnum.DROP_TRIGGER.name());
                PostgreSQLParser.NameContext name = ctx.name();
                if (Objects.nonNull(name)) {

                    currentStatement.addIdentifier(name.getText(), IdentifierTypeEnum.TRIGGER.name(), name.getStart());
                }
            } else {
                currentStatement.setType(SqlTypeEnum.DROP.name());
            }
        }
        return super.visitDropstmt(ctx);
    }

    private void visitAnyName(PostgreSQLParser.Any_nameContext ctx, IdentifierTypeEnum identifierTypeEnum) {
        splitName(identifierTypeEnum, ctx);
    }

    private void splitName(IdentifierTypeEnum identifierTypeEnum, ParserRuleContext parserRuleContext) {
        if (Objects.isNull(parserRuleContext)) {
            return;
        }
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        Try.run(() -> {
            CommonTokenStream commonTokenStream = context.getCommonTokenStream();
            List<Token> tokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(commonTokenStream, parserRuleContext);
            Token database = null, schema = null, table = null;
            String databaseText = null, schemaText = null, tableText = null;
            int size = tokensOnDefault.size();
            if (size == 5) {
                database = tokensOnDefault.get(0);
                schema = tokensOnDefault.get(2);
                databaseText = SqlStringUtil.removeQuote(database.getText());
                schemaText = SqlStringUtil.removeQuote(schema.getText());
            } else if (size == 3) {
                schema = tokensOnDefault.get(0);
                schemaText = SqlStringUtil.removeQuote(schema.getText());
            }
            table = tokensOnDefault.get(size - 1);
            tableText = SqlStringUtil.removeQuote(table.getText());

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

            Identifier identifier = new Identifier();
            identifier.setIdentifierName(tableText);
            identifier.setIdentifierType(identifierTypeEnum.name());
            identifier.setFirstToken(table);
            identifier.setIdentifierDatabase(databaseText);
            identifier.setIdentifierSchema(schemaText);
            currentStatement.addIdentifier(identifier);
        }).onFailure(e -> log.error("pgsql visitAnyName error", e));


    }

    @Override
    public Void visitViewstmt(PostgreSQLParser.ViewstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_VIEW.name());
        PostgreSQLParser.Qualified_nameContext qualifiedNameContext = ctx.qualified_name();
        visitQualifiedName(qualifiedNameContext, IdentifierTypeEnum.VIEW);
        return super.visitViewstmt(ctx);
    }

    private void visitQualifiedName(PostgreSQLParser.Qualified_nameContext ctx, IdentifierTypeEnum identifierTypeEnum) {
        splitName(identifierTypeEnum, ctx);
    }

    @Override
    public Void visitCreatefunctionstmt(PostgreSQLParser.CreatefunctionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FUNCTION.name());
        return super.visitCreatefunctionstmt(ctx);
    }

    @Override
    public Void visitFunc_name(PostgreSQLParser.Func_nameContext ctx) {
        splitName(IdentifierTypeEnum.FUNCTION, ctx);
        return super.visitFunc_name(ctx);
    }

    @Override
    public Void visitCreatestmt(PostgreSQLParser.CreatestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        PostgreSQLParser.Qualified_nameContext qualifiedNameContext = ctx.qualified_name(0);
        visitQualifiedName(qualifiedNameContext, IdentifierTypeEnum.TABLE);
        return super.visitCreatestmt(ctx);
    }

    @Override
    public Void visitRemovefuncstmt(PostgreSQLParser.RemovefuncstmtContext ctx) {
        TerminalNode routine = ctx.ROUTINE();
        TerminalNode function = ctx.FUNCTION();
        TerminalNode procedure = ctx.PROCEDURE();
        IdentifierTypeEnum identifierTypeEnum = null;
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        if (Objects.nonNull(routine)) {
            currentStatement.setType(SqlTypeEnum.DROP_ROUTINE.name());
            identifierTypeEnum = IdentifierTypeEnum.ROUTINE;
        } else if (Objects.nonNull(function)) {

            currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());
            identifierTypeEnum = IdentifierTypeEnum.FUNCTION;
        } else if (Objects.nonNull(procedure)) {

            currentStatement.setType(SqlTypeEnum.DROP_PROCEDURE.name());
            identifierTypeEnum = IdentifierTypeEnum.PROCEDURE;
        }

        currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());
        PostgreSQLParser.Function_with_argtypes_listContext functionWithArgtypesListContext = ctx.function_with_argtypes_list();
        if (Objects.nonNull(functionWithArgtypesListContext)) {
            for (PostgreSQLParser.Function_with_argtypesContext functionWithArgtypesContext :
                    functionWithArgtypesListContext.function_with_argtypes()) {
                visitFunctionName(functionWithArgtypesContext, identifierTypeEnum);
            }
        }
        return super.visitRemovefuncstmt(ctx);
    }

    private void visitFunctionName(PostgreSQLParser.Function_with_argtypesContext ctx, IdentifierTypeEnum identifierTypeEnum) {
        splitName(identifierTypeEnum, ctx);
    }

    @Override
    public Void visitDroprolestmt(PostgreSQLParser.DroprolestmtContext ctx) {
        TerminalNode role = ctx.ROLE();
        TerminalNode user = ctx.USER();
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        IdentifierTypeEnum identifierTypeEnum = null;
        if (Objects.nonNull(role)) {
            currentStatement.setType(SqlTypeEnum.DROP_ROLE.name());
            identifierTypeEnum = IdentifierTypeEnum.ROLE;
        } else if (Objects.nonNull(user)) {
            currentStatement.setType(SqlTypeEnum.DROP_USER.name());
            identifierTypeEnum = IdentifierTypeEnum.USER;
        }
        if (Objects.isNull(identifierTypeEnum)) {
            return null;
        }
        PostgreSQLParser.Role_listContext roleListContext = ctx.role_list();
        if (Objects.nonNull(roleListContext)) {
            for (PostgreSQLParser.RolespecContext rolespecContext : roleListContext.rolespec()) {
                visitRoleSpec(rolespecContext, identifierTypeEnum);
            }
        }
        return null;
    }

    private void visitRoleSpec(PostgreSQLParser.RolespecContext ctx, IdentifierTypeEnum identifierTypeEnum) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        currentStatement.addIdentifier(ctx.getText(), identifierTypeEnum.name(), ctx.getStart());
    }


    @Override
    public Void visitDropdbstmt(PostgreSQLParser.DropdbstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_DATABASE.name());
        PostgreSQLParser.NameContext name = ctx.name();
        if (Objects.nonNull(name)) {
            currentStatement.addIdentifier(name.getText(), IdentifierTypeEnum.DATABASE.name(), name.getStart());
        }
        return null;
    }

    @Override
    public Void visitSimple_select_pramary(PostgreSQLParser.Simple_select_pramaryContext ctx) {
        Try.of(() -> {
            PostgreSQLParser.From_clauseContext fromClauseContext = ctx.from_clause();
            if (Objects.isNull(fromClauseContext) || fromClauseContext.getChildCount() <= 0) {
                return super.visitSimple_select_pramary(ctx);
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

                if (Objects.nonNull(alias)) {
                    identifier.setIdentifierAlias(aliasText);
                    identifier.setLastToken(alias);
                }
                currentStatement.addIdentifier(identifier);
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitSimple_select_pramary error", e));

        return super.visitSimple_select_pramary(ctx);
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
    public Void visitInsert_column_item(PostgreSQLParser.Insert_column_itemContext ctx) {
        CommonTokenStream commonTokenStream = context.getCommonTokenStream();
        Identifier identifier = splitColumName(commonTokenStream, ctx);
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.addIdentifier(identifier);
        return super.visitInsert_column_item(ctx);
    }

    @Override
    public Void visitColumnDef(PostgreSQLParser.ColumnDefContext ctx) {
        Try.of(() -> {
            PostgreSQLParser.ColidContext colid = ctx.colid();
            if (Objects.isNull(colid)) {
                return null;
            }
            String columnName = context.getText(colid.getSourceInterval());
            if (StringUtils.isBlank(columnName)) {
                return null;
            }
            String[] split = columnName.split("\\.");
            columnName = split[split.length - 1];
            PostgreSQLParser.TypenameContext typename = ctx.typename();
            if (Objects.isNull(typename)) {
                return null;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            String dataType = context.getText(typename.getSourceInterval());
            if (StringUtils.isNotBlank(dataType)) {
                Identifier identifier = new Identifier();
                identifier.setIdentifierName(SqlStringUtil.removeQuote(columnName));
                identifier.setIdentifierType(IdentifierTypeEnum.COLUMN.name());
                identifier.setIdentifierDataType(dataType);
                currentStatement.addIdentifier(identifier);
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitColumnDef error", e));

        return null;
    }


    @Override
    public Void visitBuiltin_function_name(PostgreSQLParser.Builtin_function_nameContext ctx) {
        Token start = ctx.start;
        if (Objects.nonNull(start)) {
            Identifier identifier = new Identifier();
            identifier.setIdentifierName(start.getText());
            identifier.setIdentifierType(IdentifierTypeEnum.SYS_FUNCTION.name());
            identifier.setFirstToken(start);
        }
        return null;
    }

    @Override
    public Void visitType_function_name(PostgreSQLParser.Type_function_nameContext ctx) {
        Token start = ctx.start;
        if (Objects.nonNull(start)) {
            Identifier identifier = new Identifier();
            identifier.setIdentifierName(start.getText());
            identifier.setIdentifierType(IdentifierTypeEnum.UDF_FUNCTION.name());
            identifier.setFirstToken(start);
        }
        return null;
    }

    @Override
    public Void visitCallstmt(PostgreSQLParser.CallstmtContext ctx) {
        Try.of(() -> {
            PostgreSQLParser.Func_applicationContext funcApplicationContext = ctx.func_application();
            if (Objects.isNull(funcApplicationContext)) {
                return null;
            }
            PostgreSQLParser.Func_nameContext funcNameContext = funcApplicationContext.func_name();
            if (Objects.isNull(funcNameContext)) {
                return null;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            PostgreSQLParser.Type_function_nameContext typeFunctionNameContext = funcNameContext.type_function_name();
            if (Objects.nonNull(typeFunctionNameContext)) {
                Token start = typeFunctionNameContext.start;
                if (Objects.nonNull(start)) {
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(start.getText());
                    identifier.setIdentifierType(IdentifierTypeEnum.PROCEDURE.name());
                    identifier.setFirstToken(start);
                    currentStatement.addIdentifier(identifier);
                }
                return null;
            }
            PostgreSQLParser.ColidContext colid = funcNameContext.colid();
            PostgreSQLParser.IndirectionContext indirection = funcNameContext.indirection();
            if (Objects.nonNull(colid) && Objects.nonNull(indirection)) {
                List<Token> parserRuleTokensOnDefault = TokenUtil.getParserRuleTokensOnDefault(context.getCommonTokenStream(), indirection);
                Token database = null, schema = null, procedure = null;
                String databaseText = null, schemaText = null, procedureText = null;
                int size = parserRuleTokensOnDefault.size();
                if (size == 4) {
                    database = colid.start;
                    schema = parserRuleTokensOnDefault.get(1);
                    procedure = parserRuleTokensOnDefault.get(3);
                } else if (size == 2) {
                    schema = colid.start;
                    procedure = parserRuleTokensOnDefault.get(1);
                }
                if (Objects.nonNull(database)) {
                    databaseText = SqlStringUtil.removeQuote(database.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(databaseText);
                    identifier.setIdentifierType(IdentifierTypeEnum.DATABASE.name());
                    identifier.setFirstToken(database);
                    currentStatement.addIdentifier(identifier);
                }
                if (Objects.nonNull(schema)) {
                    schemaText = SqlStringUtil.removeQuote(schema.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(schemaText);
                    identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                    identifier.setFirstToken(schema);
                    currentStatement.addIdentifier(identifier);
                }

                if (Objects.nonNull(procedure)) {
                    procedureText = SqlStringUtil.removeQuote(procedure.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierName(procedureText);
                    identifier.setIdentifierDatabase(databaseText);
                    identifier.setIdentifierSchema(schemaText);
                    identifier.setIdentifierType(IdentifierTypeEnum.PROCEDURE.name());
                    identifier.setFirstToken(procedure);
                    currentStatement.addIdentifier(identifier);
                }
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitCallstmt error", e));

        return null;
    }
}
