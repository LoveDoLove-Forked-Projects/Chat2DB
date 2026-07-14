package ai.chat2db.plugin.mysql.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.mysql.parser.base.MySqlParserBaseVisitor;
import ai.chat2db.plugin.mysql.parser.util.MysqlStringUtil;
import ai.chat2db.spi.util.SqlCommentUtil;
import io.vavr.control.Try;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;


public class MysqlSimpleParserVisitor extends MySqlParserBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(MysqlSimpleParserVisitor.class);

    private StatementContext context;

    public StatementContext getContext() {
        return context;
    }

    public void setContext(StatementContext context) {
        this.context = context;
    }


    public MysqlSimpleParserVisitor(StatementContext context) {
        this.context = context;
    }


    @Override
    public Void visitSqlStatements(MySqlParser.SqlStatementsContext ctx) {
        List<MySqlParser.SqlStatementContext> sqlStatement = ctx.sqlStatement();
        TokenStream commonTokenStream = context.getCommonTokenStream();
        for (int i = 0; i < sqlStatement.size(); i++) {
            int commentStartIndex = 0;
            if (i != 0) {
                MySqlParser.SqlStatementContext lastChild = sqlStatement.get(i - 1);
                if (Objects.nonNull(lastChild)) {
                    commentStartIndex = lastChild.getStop().getTokenIndex();
                }
            }
            MySqlParser.SqlStatementContext child = sqlStatement.get(i);
            int commentEndIndex = child.getStart().getTokenIndex();
            String sqlComment = SqlCommentUtil.searchSqlComment(commentStartIndex, commentEndIndex, commonTokenStream);
            Statement statement = new Statement();
            statement.setStatementType(StatementValidTypeEnum.VALID.name());
            statement.setSql(commonTokenStream.getText(child.getSourceInterval()));
            statement.setFirstToken(child.getStart());
            statement.setLastToken(child.getStop());
            statement.setComment(sqlComment);
            context.setCurrentStatement(statement);
            visit(child);
            context.addStatement(statement);
        }
        return null;
    }
    @Override
    public Void visitParenthesisSelect(MySqlParser.ParenthesisSelectContext ctx) {
        context.setStatementType(SqlTypeEnum.SELECT);
        return null;
    }

    @Override
    public Void visitSimpleSelect(MySqlParser.SimpleSelectContext ctx) {
        context.setStatementType(SqlTypeEnum.SELECT);
        return null;
    }

    @Override
    public Void visitUnionSelect(MySqlParser.UnionSelectContext ctx) {
        context.setStatementType(SqlTypeEnum.SELECT);
        return null;
    }

    @Override
    public Void visitWithLateralStatement(MySqlParser.WithLateralStatementContext ctx) {
        context.setStatementType(SqlTypeEnum.SELECT);
        return null;
    }


    @Override
    public Void visitInsertStatement(MySqlParser.InsertStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        MysqlParserVisitor.visitInsertValueMappings(context, ctx);
        return null;
    }


    @Override
    public Void visitDeleteStatement(MySqlParser.DeleteStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        return null;
    }

    @Override
    public Void visitCreateDatabase(MySqlParser.CreateDatabaseContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_DATABASE);
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitCreateEvent(MySqlParser.CreateEventContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_EVENT);
        MySqlParser.FullIdContext fullIdContext = ctx.eventDeclarationName().fullId();
        visitFullIdContext(fullIdContext);
        return null;
    }

    @Override
    public Void visitCreateProcedure(MySqlParser.CreateProcedureContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_PROCEDURE);
        MySqlParser.FullIdContext fullIdContext = ctx.procedureDeclarationName().fullId();
        visitFullIdContext(fullIdContext);
        return super.visitCreateProcedure(ctx);
    }

    @Override
    public Void visitCreateFunction(MySqlParser.CreateFunctionContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_FUNCTION);
        MySqlParser.FullIdContext fullIdContext = ctx.functionDeclarationName().fullId();
        visitFullIdContext(fullIdContext);
        return super.visitCreateFunction(ctx);
    }

    @Override
    public Void visitCreateIndex(MySqlParser.CreateIndexContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_INDEX);
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        visitTableNameContext(tableNameContext);
        return null;

    }

    @Override
    public Void visitCreateRole(MySqlParser.CreateRoleContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_ROLE);
        return null;
    }


    @Override
    public Void visitCreateTrigger(MySqlParser.CreateTriggerContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_TRIGGER);
        MySqlParser.FullIdContext fullIdContext = ctx.triggerDeclarationName().fullId();
        visitFullIdContext(fullIdContext);
        return null;

    }

    @Override
    public Void visitCreateView(MySqlParser.CreateViewContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_VIEW);
        MySqlParser.FullIdContext fullIdContext = ctx.viewDeclarationName().tableName().fullId();
        visitFullIdContext(fullIdContext);
        return null;
    }

    @Override
    public Void visitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_TABLE);
        visitTableNameContext(ctx.tableDeclarationName().tableName());
        return null;
    }

    private void visitTableNameContext(MySqlParser.TableNameContext ctx) {
        Try.run(() -> {
            if (Objects.isNull(ctx)) {
                return;
            }
            Token start = ctx.start;
            Token stop = ctx.stop;
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return ;
            }
            if (start.getTokenIndex() != stop.getTokenIndex()) {
                String databaseText = MysqlStringUtil.removeQuote(start.getText());
                Identifier identifier = new Identifier();
                identifier.setIdentifierType(IdentifierTypeEnum.DATABASE.name());
                identifier.setIdentifierDatabase(databaseText);
                currentStatement.addIdentifier(identifier);
            } else {
                Identifier identifier = new Identifier();
                identifier.setIdentifierType(IdentifierTypeEnum.DATABASE.name());
                currentStatement.addIdentifier(identifier);
            }
        }).onFailure(e -> log.error("mysql visitTableName error", e));
    }

    private void visitFullIdContext(MySqlParser.FullIdContext fullIdContext) {
        Try.run(() -> {
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return ;
            }
            if (Objects.nonNull(fullIdContext)) {
                Token start = fullIdContext.start;
                Token stop = fullIdContext.stop;
                if (start.getTokenIndex() != stop.getTokenIndex()) {
                    String databaseText = MysqlStringUtil.removeQuote(start.getText());
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierDataType(IdentifierTypeEnum.DATABASE.name());
                    identifier.setIdentifierDatabase(databaseText);
                    currentStatement.addIdentifier(identifier);
                } else {
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierType(IdentifierTypeEnum.DATABASE.name());
                    currentStatement.addIdentifier(identifier);
                }
            }
        }).onFailure(e -> log.error("mysql visitFullIdContext error", e));
    }


    @Override
    public Void visitUpdateStatement(MySqlParser.UpdateStatementContext ctx) {
        context.setStatementType(SqlTypeEnum.UPDATE);
        return null;
    }

    @Override
    public Void visitUseStatement(MySqlParser.UseStatementContext ctx) {
        context.setStatementType(SqlTypeEnum.USE_DATABASE);
        MySqlParser.UidContext uid = ctx.databaseReferenceName().uid();
        if (Objects.nonNull(uid)) {
            Identifier identifier = new Identifier();
            String database = MysqlStringUtil.removeQuote(uid.getText());
            identifier.setIdentifierDatabase(database);
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return null;
            }
            currentStatement.addIdentifier(identifier);
        }
        return null;
    }

    @Override
    public Void visitDropDatabase(MySqlParser.DropDatabaseContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_DATABASE);
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.addIdentifier(new Identifier());
        return null;
    }

    @Override
    public Void visitDropIndex(MySqlParser.DropIndexContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_INDEX);
        visitTableNameContext(ctx.tableReferenceName().tableName());
        return null;
    }


    @Override
    public Void visitDropProcedure(MySqlParser.DropProcedureContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_PROCEDURE);
        MySqlParser.FullIdContext fullIdContext = ctx.procedureReferenceName().fullId();
        visitFullIdContext(fullIdContext);
        return null;
    }

    @Override
    public Void visitDropFunction(MySqlParser.DropFunctionContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_FUNCTION);
        MySqlParser.FullIdContext fullIdContext = ctx.functionReferenceName().fullId();
        visitFullIdContext(fullIdContext);
        return null;
    }


    @Override
    public Void visitDropTable(MySqlParser.DropTableContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_TABLE);
        MySqlParser.TablesContext tables = ctx.tables();
        if (Objects.nonNull(tables)) {
            for (MySqlParser.TableReferenceNameContext tableReferenceNameContext : tables.tableReferenceName()) {
                MySqlParser.TableNameContext tableNameContext = tableReferenceNameContext.tableName();
                visitTableNameContext(tableNameContext);
            }
        }
        return null;
    }

    @Override
    public Void visitDropTrigger(MySqlParser.DropTriggerContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_TRIGGER);
        MySqlParser.FullIdContext fullIdContext = ctx.triggerReferenceName().fullId();
        visitFullIdContext(fullIdContext);
        return null;
    }

    @Override
    public Void visitDropView(MySqlParser.DropViewContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_VIEW);
        for (MySqlParser.ViewReferenceNameContext viewReferenceNameContext : ctx.viewReferenceName()) {
            visitFullIdContext(viewReferenceNameContext.tableName().fullId());
        }
        return null;
    }

    @Override
    public Void visitDropRole(MySqlParser.DropRoleContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_ROLE);
        return null;
    }

    @Override
    public Void visitDropUser(MySqlParser.DropUserContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_USER);
        return null;
    }
}
