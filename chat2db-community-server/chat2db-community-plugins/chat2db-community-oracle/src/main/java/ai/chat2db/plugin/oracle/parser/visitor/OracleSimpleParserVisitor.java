package ai.chat2db.plugin.oracle.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.plugin.oracle.parser.base.PlSqlParser;
import ai.chat2db.plugin.oracle.parser.base.PlSqlParserBaseVisitor;
import ai.chat2db.spi.util.SqlCommentUtil;
import ai.chat2db.spi.util.SqlStringUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Objects;


public class OracleSimpleParserVisitor extends PlSqlParserBaseVisitor<Void> {

    private final StatementContext context;

    public OracleSimpleParserVisitor(StatementContext context) {
        this.context = context;
    }

    @Override
    public Void visitSql_script(PlSqlParser.Sql_scriptContext ctx) {

        List<PlSqlParser.Unit_statementContext> unitStatement = ctx.unit_statement();
        TokenStream commonTokenStream = context.getCommonTokenStream();
        for (int i = 0; i < unitStatement.size(); i++) {
            PlSqlParser.Unit_statementContext child = unitStatement.get(i);
            Statement statement = new Statement();
            int sqlCommentSearchStartIndex = 0;
            if (i != 0) {
                PlSqlParser.Unit_statementContext unitStatementContext = unitStatement.get(i - 1);
                if (Objects.nonNull(unitStatementContext)) {
                    sqlCommentSearchStartIndex = unitStatementContext.getStop().getTokenIndex();

                }
            }
            int sqlCommentSearchEndIndex = child.getStart().getTokenIndex();
            String sqlComment = SqlCommentUtil.searchSqlComment(sqlCommentSearchStartIndex, sqlCommentSearchEndIndex, commonTokenStream);
            statement.setComment(sqlComment);
            context.setCurrentStatement(statement);
            statement.setStatementType(StatementValidTypeEnum.VALID.name());
            statement.setSql(commonTokenStream.getText(child.getSourceInterval()));
            statement.setFirstToken(child.getStart());
            statement.setLastToken(child.getStop());
            context.addStatement(statement);
            visit(child);
        }
        return null;
    }

    @Override
    public Void visitSelect_statement(PlSqlParser.Select_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        return null;
    }

    @Override
    public Void visitExplain_statement(PlSqlParser.Explain_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.EXPLAIN.name());
        return super.visitExplain_statement(ctx);
    }

    @Override
    public Void visitAnonymous_block(PlSqlParser.Anonymous_blockContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ANONYMOUS_BLOCK.name());
        String sql = currentStatement.getSql();
        if (!sql.endsWith(";")) {
            sql = sql + ";";
            currentStatement.setSql(sql);
        }
        return super.visitAnonymous_block(ctx);
    }

    @Override
    public Void visitCreate_function_body(PlSqlParser.Create_function_bodyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FUNCTION.name());
        String sql = currentStatement.getSql();
        if (!sql.endsWith(";")) {
            sql = sql + ";";
            currentStatement.setSql(sql);
        }
        return super.visitCreate_function_body(ctx);
    }

    @Override
    public Void visitCreate_procedure_body(PlSqlParser.Create_procedure_bodyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_PROCEDURE.name());
        String sql = currentStatement.getSql();
        if (!sql.endsWith(";")) {
            sql = sql + ";";
            currentStatement.setSql(sql);
        }
        return super.visitCreate_procedure_body(ctx);
    }

    @Override
    public Void visitCreate_trigger(PlSqlParser.Create_triggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TRIGGER.name());
        String sql = currentStatement.getSql();
        if (!sql.endsWith(";")) {
            sql = sql + ";";
            currentStatement.setSql(sql);
        }
        return super.visitCreate_trigger(ctx);
    }

    @Override
    public Void visitCreate_index(PlSqlParser.Create_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_INDEX.name());
        return null;
    }

    @Override
    public Void visitCreate_user(PlSqlParser.Create_userContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }


    @Override
    public Void visitCreate_view(PlSqlParser.Create_viewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_VIEW.name());
        PlSqlParser.Schema_nameContext schemaNameContext = ctx.schema_name();
        String schemaText;
        if (Objects.nonNull(schemaNameContext)) {
            Token start = schemaNameContext.getStart();
            schemaText = SqlStringUtil.removeQuote(start.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());

            currentStatement.addIdentifier(identifier);
        } else {
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }

    @Override
    public Void visitCreate_role(PlSqlParser.Create_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ROLE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitCreate_table(PlSqlParser.Create_tableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        PlSqlParser.Schema_nameContext schemaNameContext = ctx.schema_name();
        if (Objects.nonNull(schemaNameContext)) {
            Token start = schemaNameContext.getStart();
            String schemaText = SqlStringUtil.removeQuote(start.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());

            currentStatement.addIdentifier(identifier);
        } else {
            currentStatement.addIdentifier(new Identifier());
        }
        return super.visitCreate_table(ctx);
    }

    @Override
    public Void visitCreate_database(PlSqlParser.Create_databaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_DATABASE.name());
        currentStatement.addIdentifier(new Identifier());
        return null;
    }


    @Override
    public Void visitDrop_function(PlSqlParser.Drop_functionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());
        return super.visitDrop_function(ctx);
    }

    @Override
    public Void visitDrop_procedure(PlSqlParser.Drop_procedureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_PROCEDURE.name());
        return super.visitDrop_procedure(ctx);
    }

    @Override
    public Void visitDrop_role(PlSqlParser.Drop_roleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_ROLE.name());
        return null;
    }

    @Override
    public Void visitDrop_trigger(PlSqlParser.Drop_triggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TRIGGER.name());
        return super.visitDrop_trigger(ctx);
    }


    @Override
    public Void visitDrop_user(PlSqlParser.Drop_userContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        currentStatement.addIdentifier(new Identifier());

        return null;
    }

    @Override
    public Void visitDrop_index(PlSqlParser.Drop_indexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_INDEX.name());
        return null;
    }

    @Override
    public Void visitDrop_table(PlSqlParser.Drop_tableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TABLE.name());
        PlSqlParser.Tableview_nameContext tableviewNameContext = ctx.tableview_name();
        visitTableViewName(tableviewNameContext);
        return null;
    }


    @Override
    public Void visitDrop_view(PlSqlParser.Drop_viewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_VIEW.name());
        PlSqlParser.Tableview_nameContext tableviewNameContext = ctx.tableview_name();
        visitTableViewName(tableviewNameContext);
        return super.visitDrop_view(ctx);
    }

    @Override
    public Void visitAlter_session(PlSqlParser.Alter_sessionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        PlSqlParser.Alter_session_set_clauseContext alterSessionSetClauseContext = ctx.alter_session_set_clause();
        if (Objects.isNull(alterSessionSetClauseContext)
                || alterSessionSetClauseContext.parameter_name().isEmpty()
                || !"CURRENT_SCHEMA".equalsIgnoreCase(
                        alterSessionSetClauseContext.parameter_name(0).getText())) {
            return super.visitAlter_session(ctx);
        }
        currentStatement.setType(SqlTypeEnum.SET_SCHEMA.name());
        PlSqlParser.Parameter_valueContext parameterValueContext = alterSessionSetClauseContext.parameter_value(0);
        if (Objects.nonNull(parameterValueContext)) {
            Identifier identifier = new Identifier();
            String schemaText = SqlStringUtil.removeQuote(parameterValueContext.getText());
            identifier.setIdentifierSchema(schemaText);
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }

    @Override
    public Void visitUpdate_statement(PlSqlParser.Update_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UPDATE.name());
        return null;
    }

    @Override
    public Void visitInsert_statement(PlSqlParser.Insert_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        OracleParserVisitor.visitInsertValueMappings(context, ctx);
        return null;
    }

    @Override
    public Void visitDelete_statement(PlSqlParser.Delete_statementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        return null;
    }

    private void visitTableViewName(PlSqlParser.Tableview_nameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        if (Objects.nonNull(ctx)) {
            Token start = ctx.getStart();
            Token stop = ctx.getStop();
            if (start.getTokenIndex() != stop.getTokenIndex()) {
                String schemaText = SqlStringUtil.removeQuote(start.getText());
                Identifier identifier = new Identifier();
                identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                identifier.setIdentifierSchema(schemaText);
                currentStatement.addIdentifier(identifier);
            } else {
                Identifier identifier = new Identifier();
                identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
                currentStatement.addIdentifier(identifier);
            }

        }
    }

    @Override
    public Void visitFunction_name(PlSqlParser.Function_nameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() != stop.getTokenIndex()) {
            String schemaText = SqlStringUtil.removeQuote(start.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            identifier.setIdentifierSchema(schemaText);
            currentStatement.addIdentifier(identifier);
        } else {
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }

    @Override
    public Void visitProcedure_name(PlSqlParser.Procedure_nameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() != stop.getTokenIndex()) {
            String schemaText = SqlStringUtil.removeQuote(start.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            identifier.setIdentifierSchema(schemaText);
            currentStatement.addIdentifier(identifier);
        } else {
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }

    @Override
    public Void visitTrigger_name(PlSqlParser.Trigger_nameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start.getTokenIndex() != stop.getTokenIndex()) {
            String schemaText = SqlStringUtil.removeQuote(start.getText());
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            identifier.setIdentifierSchema(schemaText);
            currentStatement.addIdentifier(identifier);
        } else {
            Identifier identifier = new Identifier();
            identifier.setIdentifierType(IdentifierTypeEnum.SCHEMA.name());
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }


}
