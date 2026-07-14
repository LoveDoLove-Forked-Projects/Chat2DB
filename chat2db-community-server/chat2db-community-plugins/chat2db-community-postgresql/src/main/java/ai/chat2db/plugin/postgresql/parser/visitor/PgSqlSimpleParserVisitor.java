package ai.chat2db.plugin.postgresql.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParser;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParserBaseVisitor;
import ai.chat2db.spi.util.SqlCommentUtil;
import ai.chat2db.spi.util.SqlStringUtil;
import ai.chat2db.spi.util.TokenUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Objects;


@Slf4j
public class PgSqlSimpleParserVisitor extends PostgreSQLParserBaseVisitor<Void> {


    private final StatementContext context;

    public PgSqlSimpleParserVisitor(StatementContext context) {
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
            int sqlCommentSearchEndIndex = child.getStart().getTokenIndex();
            String sqlComment = SqlCommentUtil.searchSqlComment(sqlCommentSearchStartIndex, sqlCommentSearchEndIndex, commonTokenStream);
            statement.setComment(sqlComment);
            statement.setSql(commonTokenStream.getText(child.getSourceInterval()));
            statement.setStatementType(StatementValidTypeEnum.VALID.name());
            statement.setFirstToken(child.getStart());
            statement.setLastToken(child.getStop());
            context.setCurrentStatement(statement);
            context.addStatement(statement);
            visit(child);
        }
        return null;
    }

    @Override
    public Void visitCreaterolestmt(PostgreSQLParser.CreaterolestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ROLE.name());
        return null;
    }


    @Override
    public Void visitCreateuserstmt(PostgreSQLParser.CreateuserstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        return null;
    }

    @Override
    public Void visitCreateschemastmt(PostgreSQLParser.CreateschemastmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_SCHEMA.name());
        Identifier identifier = new Identifier();
        identifier.setIdentifierType(IdentifierTypeEnum.DATABASE.name());
        currentStatement.addIdentifier(identifier);
        return null;
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
                            String schemaText = SqlStringUtil.removeQuote(value.getText());
                            Identifier identifier = new Identifier();
                            identifier.setIdentifierSchema(schemaText);
                            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                            currentStatement.addIdentifier(identifier);
                        }
                    }
                }
            }
            return null;
        }).onFailure(e -> log.error("pgsql visitVariablesetstmt error", e));

        return null;
    }

    @Override
    public Void visitInsertstmt(PostgreSQLParser.InsertstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        PgsqlParserVisitor.visitInsertValueMappings(context, ctx);
        return null;
    }

    @Override
    public Void visitSelectstmt(PostgreSQLParser.SelectstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        PostgreSQLParser.Select_no_parensContext selectNoParensContext = ctx.select_no_parens();
        if (selectNoParensContext != null) {
            PostgreSQLParser.Select_clauseContext selectClauseContext = selectNoParensContext.select_clause();
            if (selectClauseContext != null) {
                for (PostgreSQLParser.Simple_select_intersectContext simpleSelectIntersectContext : selectClauseContext.simple_select_intersect()) {
                    for (PostgreSQLParser.Simple_select_pramaryContext simpleSelectPramaryContext : simpleSelectIntersectContext.simple_select_pramary()) {
                        for (PostgreSQLParser.Into_clauseContext intoClauseContext : simpleSelectPramaryContext.into_clause()) {
                            TerminalNode into = intoClauseContext.INTO();
                            if (Objects.nonNull(into)) {
                                currentStatement.setType(SqlTypeEnum.SELECT_INTO.name());
                                break;
                            }
                        }

                    }

                }
            }
        }

        currentStatement.setType(SqlTypeEnum.SELECT.name());
        return null;
    }

    @Override
    public Void visitUpdatestmt(PostgreSQLParser.UpdatestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UPDATE.name());
        return null;
    }

    @Override
    public Void visitDeletestmt(PostgreSQLParser.DeletestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        return null;
    }

    @Override
    public Void visitDropstmt(PostgreSQLParser.DropstmtContext ctx) {
        PostgreSQLParser.Object_type_any_nameContext objectTypeAnyNameContext = ctx.object_type_any_name();
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        if (Objects.nonNull(objectTypeAnyNameContext)) {
            String text = objectTypeAnyNameContext.getText();
            if (Objects.equals(IdentifierTypeEnum.TABLE.name(), text)) {
                currentStatement.setType(SqlTypeEnum.DROP_TABLE.name());

            } else if (Objects.equals(IdentifierTypeEnum.VIEW.name(), text)) {
                currentStatement.setType(SqlTypeEnum.DROP_VIEW.name());

            } else if (Objects.equals(IdentifierTypeEnum.TRIGGER.name(), text)) {
                currentStatement.setType(SqlTypeEnum.DROP_TRIGGER.name());
            } else {
                currentStatement.setType(SqlTypeEnum.DROP.name());
            }
        }
        return null;
    }

    @Override
    public Void visitViewstmt(PostgreSQLParser.ViewstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_VIEW.name());
        PostgreSQLParser.Qualified_nameContext qualifiedNameContext = ctx.qualified_name();
        visitQualifiedName(qualifiedNameContext);
        return super.visitViewstmt(ctx);
    }

    private void visitQualifiedName(PostgreSQLParser.Qualified_nameContext ctx) {
        splitName(ctx);
    }

    private void splitName(ParserRuleContext parserRuleContext) {
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
            Token database, schema;
            String databaseText = null, schemaText = null;
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
            Identifier identifier = new Identifier();
            identifier.setIdentifierDatabase(databaseText);
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }).onFailure(e -> log.error("pgsql splitName error", e));


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
        splitName(ctx);
        return null;
    }


    @Override
    public Void visitCreatestmt(PostgreSQLParser.CreatestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        PostgreSQLParser.Qualified_nameContext qualifiedNameContext = ctx.qualified_name(0);
        visitQualifiedName(qualifiedNameContext);
        return super.visitCreatestmt(ctx);
    }

    @Override
    public Void visitRemovefuncstmt(PostgreSQLParser.RemovefuncstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        TerminalNode routine = ctx.ROUTINE();
        TerminalNode function = ctx.FUNCTION();
        TerminalNode procedure = ctx.PROCEDURE();
        if (Objects.nonNull(routine)) {
            currentStatement.setType(SqlTypeEnum.DROP_ROUTINE.name());
        } else if (Objects.nonNull(function)) {

            currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());

        } else if (Objects.nonNull(procedure)) {

            currentStatement.setType(SqlTypeEnum.DROP_PROCEDURE.name());
        }
        currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());
        PostgreSQLParser.Function_with_argtypes_listContext functionWithArgtypesListContext = ctx.function_with_argtypes_list();
        if (Objects.nonNull(functionWithArgtypesListContext)) {
            for (PostgreSQLParser.Function_with_argtypesContext functionWithArgtypesContext :
                    functionWithArgtypesListContext.function_with_argtypes()) {
                visitFunctionName(functionWithArgtypesContext);
            }
        }
        return null;
    }

    private void visitFunctionName(PostgreSQLParser.Function_with_argtypesContext ctx) {
        splitName(ctx);
    }

    @Override
    public Void visitDroprolestmt(PostgreSQLParser.DroprolestmtContext ctx) {
        TerminalNode role = ctx.ROLE();
        TerminalNode user = ctx.USER();
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        if (Objects.nonNull(role)) {
            currentStatement.setType(SqlTypeEnum.DROP_ROLE.name());
        } else if (Objects.nonNull(user)) {
            currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        }
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitDropdbstmt(PostgreSQLParser.DropdbstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_DATABASE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }
}
