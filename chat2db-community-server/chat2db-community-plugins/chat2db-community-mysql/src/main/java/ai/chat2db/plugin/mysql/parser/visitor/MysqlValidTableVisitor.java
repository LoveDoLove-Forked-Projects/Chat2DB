package ai.chat2db.plugin.mysql.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.StatementValidTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.mysql.parser.base.MySqlParserBaseVisitor;
import ai.chat2db.plugin.mysql.parser.listener.MysqlSelectListener;
import ai.chat2db.plugin.mysql.parser.util.MysqlStringUtil;
import io.vavr.control.Try;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class MysqlValidTableVisitor extends MySqlParserBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(MysqlValidTableVisitor.class);

    private final StatementContext context;

    private final MysqlSelectListener mysqlSelectListener;


    public MysqlValidTableVisitor(StatementContext context) {
        this.context = context;
        mysqlSelectListener = new MysqlSelectListener(context);
    }


    @Override
    public Void visitSqlStatements(MySqlParser.SqlStatementsContext ctx) {
        List<MySqlParser.SqlStatementContext> sqlStatement = ctx.sqlStatement();
        TokenStream commonTokenStream = context.getCommonTokenStream();
        for (MySqlParser.SqlStatementContext child : sqlStatement) {
            Statement statement = new Statement();
            statement.setStatementType(StatementValidTypeEnum.VALID.name());
            statement.setSql(commonTokenStream.getText(child.getSourceInterval()));
            statement.setFirstToken(child.getStart());
            statement.setLastToken(child.getStop());
            context.setCurrentStatement(statement);
            visit(child);
            context.addStatement(statement);
        }
        return null;
    }


    @Override
    public Void visitTableName(MySqlParser.TableNameContext ctx) {
        Try.run(() -> {
            if (Objects.isNull(ctx)) {
                return;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            Token start = ctx.start;
            Token stop = ctx.stop;
            if (start.getTokenIndex() == stop.getTokenIndex()) {
                currentStatement.addIdentifier(MysqlStringUtil.removeQuote(start.getText()), IdentifierTypeEnum.TABLE.name(), start);
            } else {
                String databaseText = MysqlStringUtil.removeQuote(start.getText());
                Identifier identifier = new Identifier();
                String tableText = stop.getText();
                if (tableText.trim().startsWith(".")) {
                    tableText = tableText.substring(1);
                }
                identifier.setIdentifierName(MysqlStringUtil.removeQuote(tableText));
                identifier.setIdentifierType(IdentifierTypeEnum.TABLE.name());
                identifier.setFirstToken(stop);
                identifier.setIdentifierDatabase(databaseText);
                currentStatement.addIdentifier(identifier);
            }
        }).onFailure(e -> log.error("mysql visitTableName error", e));

        return null;
    }

    @Override
    public Void visitCreateDatabase(MySqlParser.CreateDatabaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_DATABASE.name());
        return super.visitCreateDatabase(ctx);
    }

    @Override
    public Void visitCreateEvent(MySqlParser.CreateEventContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_EVENT.name());
        return super.visitCreateEvent(ctx);
    }

    @Override
    public Void visitCreateIndex(MySqlParser.CreateIndexContext ctx) {
        context.setStatementType(SqlTypeEnum.CREATE_INDEX);
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        visitTableNameContext(tableNameContext);
        return null;

    }

    @Override
    public Void visitCreateLogfileGroup(MySqlParser.CreateLogfileGroupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_LOGFILE_GROUP.name());
        return super.visitCreateLogfileGroup(ctx);
    }

    @Override
    public Void visitCreateProcedure(MySqlParser.CreateProcedureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_PROCEDURE.name());
        return super.visitCreateProcedure(ctx);
    }

    @Override
    public Void visitCreateFunction(MySqlParser.CreateFunctionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FUNCTION.name());
        return super.visitCreateFunction(ctx);
    }

    @Override
    public Void visitCreateRole(MySqlParser.CreateRoleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ROLE.name());
        return super.visitCreateRole(ctx);
    }

    @Override
    public Void visitCreateServer(MySqlParser.CreateServerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_SERVER.name());
        return super.visitCreateServer(ctx);
    }

    @Override
    public Void visitCreateTablespaceInnodb(MySqlParser.CreateTablespaceInnodbContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLESPACE.name());
        return super.visitCreateTablespaceInnodb(ctx);
    }

    @Override
    public Void visitCreateTablespaceNdb(MySqlParser.CreateTablespaceNdbContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLESPACE.name());
        return super.visitCreateTablespaceNdb(ctx);
    }

    @Override
    public Void visitCreateTrigger(MySqlParser.CreateTriggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TRIGGER.name());
        return super.visitCreateTrigger(ctx);
    }

    @Override
    public Void visitCreateView(MySqlParser.CreateViewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_VIEW.name());
        return super.visitCreateView(ctx);
    }

    @Override
    public Void visitCopyCreateTable(MySqlParser.CopyCreateTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        return super.visitCopyCreateTable(ctx);
    }

    @Override
    public Void visitQueryCreateTable(MySqlParser.QueryCreateTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        return super.visitQueryCreateTable(ctx);
    }

    @Override
    public Void visitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        return super.visitColumnCreateTable(ctx);
    }

    @Override
    public Void visitAlterSimpleDatabase(MySqlParser.AlterSimpleDatabaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_DATABASE.name());
        return super.visitAlterSimpleDatabase(ctx);
    }

    @Override
    public Void visitAlterUpgradeName(MySqlParser.AlterUpgradeNameContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_DATABASE.name());
        return super.visitAlterUpgradeName(ctx);
    }

    @Override
    public Void visitAlterEvent(MySqlParser.AlterEventContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_EVENT.name());
        return super.visitAlterEvent(ctx);
    }

    @Override
    public Void visitAlterFunction(MySqlParser.AlterFunctionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_FUNCTION.name());
        return super.visitAlterFunction(ctx);
    }

    @Override
    public Void visitAlterInstance(MySqlParser.AlterInstanceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_INSTANCE.name());
        return super.visitAlterInstance(ctx);
    }

    @Override
    public Void visitAlterLogfileGroup(MySqlParser.AlterLogfileGroupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_LOGFILE_GROUP.name());
        return super.visitAlterLogfileGroup(ctx);
    }

    @Override
    public Void visitAlterProcedure(MySqlParser.AlterProcedureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_PROCEDURE.name());
        return super.visitAlterProcedure(ctx);
    }

    @Override
    public Void visitAlterServer(MySqlParser.AlterServerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_SERVER.name());
        return super.visitAlterServer(ctx);
    }

    @Override
    public Void visitAlterTable(MySqlParser.AlterTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_TABLE.name());
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        visitTableNameContext(tableNameContext);
        return null;
    }

    @Override
    public Void visitAlterTablespace(MySqlParser.AlterTablespaceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_TABLESPACE.name());
        return super.visitAlterTablespace(ctx);
    }

    @Override
    public Void visitAlterView(MySqlParser.AlterViewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_VIEW.name());
        return super.visitAlterView(ctx);
    }


    @Override
    public Void visitDropDatabase(MySqlParser.DropDatabaseContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_DATABASE.name());
        return super.visitDropDatabase(ctx);
    }

    @Override
    public Void visitDropEvent(MySqlParser.DropEventContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_EVENT.name());
        return super.visitDropEvent(ctx);
    }

    @Override
    public Void visitDropIndex(MySqlParser.DropIndexContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_INDEX.name());
        return super.visitDropIndex(ctx);
    }

    @Override
    public Void visitDropLogfileGroup(MySqlParser.DropLogfileGroupContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_LOGFILE_GROUP.name());
        return super.visitDropLogfileGroup(ctx);
    }

    @Override
    public Void visitDropProcedure(MySqlParser.DropProcedureContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_PROCEDURE.name());
        return super.visitDropProcedure(ctx);
    }

    @Override
    public Void visitDropFunction(MySqlParser.DropFunctionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_FUNCTION.name());
        return super.visitDropFunction(ctx);
    }

    @Override
    public Void visitDropServer(MySqlParser.DropServerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_SERVER.name());
        return super.visitDropServer(ctx);
    }

    @Override
    public Void visitDropTable(MySqlParser.DropTableContext ctx) {
        context.setStatementType(SqlTypeEnum.DROP_TABLE);
        MySqlParser.TablesContext tables = ctx.tables();
        visitTablesContext(tables);
        return null;
    }

    private void visitTablesContext(MySqlParser.TablesContext tables) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return;
        }
        if (Objects.nonNull(tables)) {
            for (MySqlParser.TableReferenceNameContext tableReferenceNameContext : tables.tableReferenceName()) {
                MySqlParser.TableNameContext tableNameContext = tableReferenceNameContext.tableName();
                Token start = tableNameContext.start;
                currentStatement.addIdentifier(MysqlStringUtil.removeQuote(start.getText()), IdentifierTypeEnum.TABLE.name(), start);
            }
        }
    }

    @Override
    public Void visitDropTablespace(MySqlParser.DropTablespaceContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TABLESPACE.name());
        return super.visitDropTablespace(ctx);
    }

    @Override
    public Void visitDropTrigger(MySqlParser.DropTriggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TRIGGER.name());
        return super.visitDropTrigger(ctx);
    }

    @Override
    public Void visitDropView(MySqlParser.DropViewContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_VIEW.name());
        return super.visitDropView(ctx);
    }

    @Override
    public Void visitDropRole(MySqlParser.DropRoleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_ROLE.name());
        return super.visitDropRole(ctx);
    }

    @Override
    public Void visitSetRole(MySqlParser.SetRoleContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_ROLE.name());
        return super.visitSetRole(ctx);
    }

    @Override
    public Void visitRenameTable(MySqlParser.RenameTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RENAME_TABLE.name());
        return super.visitRenameTable(ctx);
    }

    @Override
    public Void visitTruncateTable(MySqlParser.TruncateTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.TRUNCATE_TABLE.name());
        return super.visitTruncateTable(ctx);
    }

    @Override
    public Void visitParenthesisSelect(MySqlParser.ParenthesisSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        mysqlSelectListener.parserSelectStatement(ctx);
        return null;
    }

    @Override
    public Void visitSimpleSelect(MySqlParser.SimpleSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        mysqlSelectListener.parserSelectStatement(ctx);
        return null;
    }

    @Override
    public Void visitUnionSelect(MySqlParser.UnionSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        mysqlSelectListener.parserSelectStatement(ctx);
        return null;
    }

    @Override
    public Void visitWithLateralStatement(MySqlParser.WithLateralStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        mysqlSelectListener.parserSelectStatement(ctx);
        return null;
    }

    @Override
    public Void visitUnionParenthesisSelect(MySqlParser.UnionParenthesisSelectContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        mysqlSelectListener.parserSelectStatement(ctx);
        return null;
    }

    @Override
    public Void visitInsertStatement(MySqlParser.InsertStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        visitTableNameContext(tableNameContext);
        return super.visitInsertStatement(ctx);
    }


    private void visitTableNameContext(MySqlParser.TableNameContext ctx) {
        Try.run(() -> {
            if (Objects.isNull(ctx)) {
                return;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            Token start = ctx.start;
            Token stop = ctx.stop;
            if (start.getTokenIndex() == stop.getTokenIndex()) {
                currentStatement.addIdentifier(MysqlStringUtil.removeQuote(start.getText()), IdentifierTypeEnum.TABLE.name(), start);
            } else {
                String databaseText = MysqlStringUtil.removeQuote(start.getText());
                currentStatement.addIdentifier(databaseText, IdentifierTypeEnum.DATABASE.name(), start);
                Identifier identifier = new Identifier();
                String tableText = stop.getText();
                if (tableText.trim().startsWith(".")) {
                    tableText = tableText.substring(1);
                }
                identifier.setIdentifierName(MysqlStringUtil.removeQuote(tableText));
                identifier.setIdentifierType(IdentifierTypeEnum.TABLE.name());
                identifier.setFirstToken(stop);
                identifier.setIdentifierDatabase(databaseText);
                currentStatement.addIdentifier(identifier);
            }
        }).onFailure(e -> log.error("visit mysql visitTableName error", e));
    }

    @Override
    public Void visitSingleDeleteStatement(MySqlParser.SingleDeleteStatementContext ctx) {
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        visitTableNameContext(tableNameContext);
        return super.visitSingleDeleteStatement(ctx);
    }


    @Override
    public Void visitSingleUpdateStatement(MySqlParser.SingleUpdateStatementContext ctx) {
        MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
        visitTableNameContext(tableNameContext);
        return super.visitSingleUpdateStatement(ctx);
    }
    @Override
    public Void visitAtomTableItem(MySqlParser.AtomTableItemContext ctx) {
        Try.run(() -> {
            MySqlParser.TableNameContext tableNameContext = ctx.tableReferenceName().tableName();
            MySqlParser.UidContext uid = ctx.aliasDeclarationName() == null ? null : ctx.aliasDeclarationName().uid();
            if (Objects.isNull(uid) || Objects.isNull(tableNameContext)) {
                visitTableNameContext(tableNameContext);
                return;
            }
            Statement currentStatement = context.getCurrentStatement();
            if (Objects.isNull(currentStatement)) {
                return;
            }
            Token start = tableNameContext.start;
            Token stop = tableNameContext.stop;
            Token uidStart = uid.getStart();
            String tableText = stop.getText();
            if (tableText.trim().startsWith(".")) {
                tableText = tableText.substring(1);
            }
            Identifier identifier = new Identifier();
            if (start.getTokenIndex() != stop.getTokenIndex()) {
                String databaseText = start.getText();
                currentStatement.addIdentifier(databaseText, IdentifierTypeEnum.DATABASE.name(), start);
                identifier.setIdentifierDatabase(databaseText);
                identifier.setIdentifierName(MysqlStringUtil.removeQuote(tableText));
            }
            identifier.setIdentifierName(MysqlStringUtil.removeQuote(tableText));
            identifier.setIdentifierType(IdentifierTypeEnum.TABLE.name());
            identifier.setFirstToken(stop);
            identifier.setIdentifierAlias(uid.getText());
            identifier.setLastToken(uidStart);
            currentStatement.addIdentifier(identifier);

        }).onFailure(e -> log.error("mysql visitAtomTableItem error", e));
        return null;
    }


    @Override
    public Void visitUpdateStatement(MySqlParser.UpdateStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UPDATE.name());
        return super.visitUpdateStatement(ctx);
    }


    @Override
    public Void visitDeleteStatement(MySqlParser.DeleteStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DELETE.name());
        return super.visitDeleteStatement(ctx);
    }

    @Override
    public Void visitReplaceStatement(MySqlParser.ReplaceStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REPLACE_INTO.name());
        return super.visitReplaceStatement(ctx);
    }

    @Override
    public Void visitCallStatement(MySqlParser.CallStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CALL.name());
        return super.visitCallStatement(ctx);
    }

    @Override
    public Void visitLoadDataStatement(MySqlParser.LoadDataStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LOAD_DATA.name());
        return super.visitLoadDataStatement(ctx);
    }

    @Override
    public Void visitLoadXmlStatement(MySqlParser.LoadXmlStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LOAD_XML.name());
        return super.visitLoadXmlStatement(ctx);
    }


    @Override
    public Void visitDoStatement(MySqlParser.DoStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DO.name());
        return super.visitDoStatement(ctx);
    }


    @Override
    public Void visitHandlerStatement(MySqlParser.HandlerStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.HANDLER.name());
        return super.visitHandlerStatement(ctx);
    }

    @Override
    public Void visitValuesStatement(MySqlParser.ValuesStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.VALUES.name());
        return super.visitValuesStatement(ctx);
    }

    @Override
    public Void visitTableStatement(MySqlParser.TableStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.TABLE.name());
        return super.visitTableStatement(ctx);
    }

    @Override
    public Void visitStartTransaction(MySqlParser.StartTransactionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.START_TRANSACTION.name());
        return super.visitStartTransaction(ctx);
    }


    @Override
    public Void visitBeginWork(MySqlParser.BeginWorkContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.BEGIN_WORK.name());
        return super.visitBeginWork(ctx);
    }

    @Override
    public Void visitRollbackWork(MySqlParser.RollbackWorkContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ROLLBACK_WORK.name());
        return super.visitRollbackWork(ctx);
    }

    @Override
    public Void visitRollbackStatement(MySqlParser.RollbackStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ROLLBACK.name());
        return super.visitRollbackStatement(ctx);
    }


    @Override
    public Void visitSavepointStatement(MySqlParser.SavepointStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SAVEPOINT.name());
        return super.visitSavepointStatement(ctx);
    }

    @Override
    public Void visitReleaseStatement(MySqlParser.ReleaseStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RELEASE_SAVEPOINT.name());
        return super.visitReleaseStatement(ctx);
    }

    @Override
    public Void visitLockTables(MySqlParser.LockTablesContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LOCK_TABLES.name());
        return super.visitLockTables(ctx);
    }

    @Override
    public Void visitUnlockTables(MySqlParser.UnlockTablesContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UNLOCK_TABLES.name());
        return super.visitUnlockTables(ctx);
    }


    @Override
    public Void visitChangeMaster(MySqlParser.ChangeMasterContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CHANGE_MASTER.name());
        return super.visitChangeMaster(ctx);
    }


    @Override
    public Void visitChangeReplicationFilter(MySqlParser.ChangeReplicationFilterContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CHANGE_REPLICATION_FILTER.name());
        return super.visitChangeReplicationFilter(ctx);
    }


    @Override
    public Void visitPurgeBinaryLogs(MySqlParser.PurgeBinaryLogsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.PURGE_BINARY_LOGS.name());
        return super.visitPurgeBinaryLogs(ctx);
    }

    @Override
    public Void visitResetMaster(MySqlParser.ResetMasterContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RESET_MASTER.name());
        return super.visitResetMaster(ctx);
    }

    @Override
    public Void visitResetSlave(MySqlParser.ResetSlaveContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RESET_SLAVE.name());
        return super.visitResetSlave(ctx);
    }

    @Override
    public Void visitStartSlave(MySqlParser.StartSlaveContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.START_SLAVE.name());
        return super.visitStartSlave(ctx);
    }

    @Override
    public Void visitStopSlave(MySqlParser.StopSlaveContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.STOP_SLAVE.name());
        return super.visitStopSlave(ctx);
    }

    @Override
    public Void visitStartGroupReplication(MySqlParser.StartGroupReplicationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.START_GROUP_REPLICATION.name());
        return super.visitStartGroupReplication(ctx);
    }

    @Override
    public Void visitStopGroupReplication(MySqlParser.StopGroupReplicationContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.STOP_GROUP_REPLICATION.name());
        return super.visitStopGroupReplication(ctx);
    }

    @Override
    public Void visitXaStartTransaction(MySqlParser.XaStartTransactionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.XA_START.name());
        return super.visitXaStartTransaction(ctx);
    }

    @Override
    public Void visitXaEndTransaction(MySqlParser.XaEndTransactionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.XA_END.name());
        return super.visitXaEndTransaction(ctx);
    }

    @Override
    public Void visitXaPrepareStatement(MySqlParser.XaPrepareStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.XA_PREPARE.name());
        return super.visitXaPrepareStatement(ctx);
    }

    @Override
    public Void visitXaCommitWork(MySqlParser.XaCommitWorkContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.XA_COMMIT.name());
        return super.visitXaCommitWork(ctx);
    }

    @Override
    public Void visitXaRollbackWork(MySqlParser.XaRollbackWorkContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.XA_ROLLBACK.name());
        return super.visitXaRollbackWork(ctx);
    }

    @Override
    public Void visitXaRecoverWork(MySqlParser.XaRecoverWorkContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.XA_RECOVER.name());
        return super.visitXaRecoverWork(ctx);
    }


    @Override
    public Void visitPrepareStatement(MySqlParser.PrepareStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.PREPARE.name());
        return super.visitPrepareStatement(ctx);
    }

    @Override
    public Void visitExecuteStatement(MySqlParser.ExecuteStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.EXECUTE.name());
        return super.visitExecuteStatement(ctx);
    }


    @Override
    public Void visitDeallocatePrepare(MySqlParser.DeallocatePrepareContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DEALLOCATE_PREPARE.name());
        return super.visitDeallocatePrepare(ctx);
    }

    @Override
    public Void visitDetailRevoke(MySqlParser.DetailRevokeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REVOKE.name());
        return super.visitDetailRevoke(ctx);
    }

    @Override
    public Void visitShortRevoke(MySqlParser.ShortRevokeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REVOKE.name());
        return super.visitShortRevoke(ctx);
    }

    @Override
    public Void visitRoleRevoke(MySqlParser.RoleRevokeContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REVOKE.name());
        return super.visitRoleRevoke(ctx);
    }

    @Override
    public Void visitRevokeProxy(MySqlParser.RevokeProxyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REVOKE.name());
        return super.visitRevokeProxy(ctx);
    }

    @Override
    public Void visitAlterUserMysqlV56(MySqlParser.AlterUserMysqlV56Context ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_USER.name());
        return super.visitAlterUserMysqlV56(ctx);
    }

    @Override
    public Void visitAlterUserMysqlV80(MySqlParser.AlterUserMysqlV80Context ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_USER.name());
        return super.visitAlterUserMysqlV80(ctx);
    }

    @Override
    public Void visitCreateUserMysqlV56(MySqlParser.CreateUserMysqlV56Context ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        return super.visitCreateUserMysqlV56(ctx);
    }

    @Override
    public Void visitCreateUserMysqlV80(MySqlParser.CreateUserMysqlV80Context ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_USER.name());
        return super.visitCreateUserMysqlV80(ctx);
    }


    @Override
    public Void visitDropUser(MySqlParser.DropUserContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER.name());
        return super.visitDropUser(ctx);
    }

    @Override
    public Void visitGrantStatement(MySqlParser.GrantStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.GRANT.name());
        return super.visitGrantStatement(ctx);
    }

    @Override
    public Void visitGrantProxy(MySqlParser.GrantProxyContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.GRANT.name());
        return super.visitGrantProxy(ctx);
    }


    @Override
    public Void visitRenameUser(MySqlParser.RenameUserContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RENAME_USER.name());
        return super.visitRenameUser(ctx);
    }


    @Override
    public Void visitCheckTableConstraint(MySqlParser.CheckTableConstraintContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CHECK_TABLE.name());
        return super.visitCheckTableConstraint(ctx);
    }

    @Override
    public Void visitChecksumTable(MySqlParser.ChecksumTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CHECKSUM_TABLE.name());
        return super.visitChecksumTable(ctx);
    }

    @Override
    public Void visitOptimizeTable(MySqlParser.OptimizeTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.OPTIMIZE_TABLE.name());
        return super.visitOptimizeTable(ctx);
    }

    @Override
    public Void visitRepairTable(MySqlParser.RepairTableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REPAIR_TABLE.name());
        return super.visitRepairTable(ctx);
    }


    @Override
    public Void visitCreateUdfunction(MySqlParser.CreateUdfunctionContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_UDF.name());
        return super.visitCreateUdfunction(ctx);
    }


    @Override
    public Void visitInstallPlugin(MySqlParser.InstallPluginContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSTALL_PLUGIN.name());
        return super.visitInstallPlugin(ctx);
    }


    @Override
    public Void visitUninstallPlugin(MySqlParser.UninstallPluginContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UNINSTALL_PLUGIN.name());
        return super.visitUninstallPlugin(ctx);
    }


    @Override
    public Void visitSetTransactionStatement(MySqlParser.SetTransactionStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_TRANSACTION.name());
        return super.visitSetTransactionStatement(ctx);
    }

    @Override
    public Void visitSetAutocommitStatement(MySqlParser.SetAutocommitStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_AUTOCOMMIT.name());
        return super.visitSetAutocommitStatement(ctx);
    }

    @Override
    public Void visitSetPasswordStatement(MySqlParser.SetPasswordStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_PASSWORD.name());
        return super.visitSetPasswordStatement(ctx);
    }


    @Override
    public Void visitSetVariable(MySqlParser.SetVariableContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_VARIABLE.name());
        return super.visitSetVariable(ctx);
    }

    @Override
    public Void visitCharSet(MySqlParser.CharSetContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_CHARSET.name());
        return super.visitCharSet(ctx);
    }

    @Override
    public Void visitSetAutocommit(MySqlParser.SetAutocommitContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_AUTOCOMMIT.name());
        return super.visitSetAutocommit(ctx);
    }

    @Override
    public Void visitSetNewValueInsideTrigger(MySqlParser.SetNewValueInsideTriggerContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SET_VARIABLE.name());
        return super.visitSetNewValueInsideTrigger(ctx);
    }

    @Override
    public Void visitShowMasterLogs(MySqlParser.ShowMasterLogsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_MASTER_LOGS.name());
        return super.visitShowMasterLogs(ctx);
    }

    @Override
    public Void visitShowLogEvents(MySqlParser.ShowLogEventsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_LOG_EVENTS.name());
        return super.visitShowLogEvents(ctx);
    }

    @Override
    public Void visitShowObjectFilter(MySqlParser.ShowObjectFilterContext ctx) {
        return super.visitShowObjectFilter(ctx);
    }

    @Override
    public Void visitShowColumns(MySqlParser.ShowColumnsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_COLUMNS.name());
        return super.visitShowColumns(ctx);
    }

    @Override
    public Void visitShowCreateDb(MySqlParser.ShowCreateDbContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_CREATE_DB.name());
        return super.visitShowCreateDb(ctx);
    }

    @Override
    public Void visitShowCreateUser(MySqlParser.ShowCreateUserContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_CREATE_USER.name());
        return super.visitShowCreateUser(ctx);
    }

    @Override
    public Void visitShowEngine(MySqlParser.ShowEngineContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_ENGINE.name());
        return super.visitShowEngine(ctx);
    }

    @Override
    public Void visitShowGlobalInfo(MySqlParser.ShowGlobalInfoContext ctx) {
        return super.visitShowGlobalInfo(ctx);
    }

    @Override
    public Void visitShowErrors(MySqlParser.ShowErrorsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_ERRORS.name());
        return super.visitShowErrors(ctx);
    }

    @Override
    public Void visitShowCountErrors(MySqlParser.ShowCountErrorsContext ctx) {
        return super.visitShowCountErrors(ctx);
    }

    @Override
    public Void visitShowSchemaFilter(MySqlParser.ShowSchemaFilterContext ctx) {
        return super.visitShowSchemaFilter(ctx);
    }

    @Override
    public Void visitShowGrants(MySqlParser.ShowGrantsContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_GRANTS.name());
        return super.visitShowGrants(ctx);
    }

    @Override
    public Void visitShowIndexes(MySqlParser.ShowIndexesContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_INDEXES.name());
        return super.visitShowIndexes(ctx);
    }

    @Override
    public Void visitShowOpenTables(MySqlParser.ShowOpenTablesContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_OPEN_TABLES.name());
        return super.visitShowOpenTables(ctx);
    }

    @Override
    public Void visitShowProfile(MySqlParser.ShowProfileContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_PROFILE.name());
        return super.visitShowProfile(ctx);
    }

    @Override
    public Void visitShowSlaveStatus(MySqlParser.ShowSlaveStatusContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHOW_SLAVE_STATUS.name());
        return super.visitShowSlaveStatus(ctx);
    }

    @Override
    public Void visitShowCommonEntity(MySqlParser.ShowCommonEntityContext ctx) {
        return super.visitShowCommonEntity(ctx);
    }

    @Override
    public Void visitShowFilter(MySqlParser.ShowFilterContext ctx) {
        return super.visitShowFilter(ctx);
    }

    @Override
    public Void visitShowSchemaEntity(MySqlParser.ShowSchemaEntityContext ctx) {
        return super.visitShowSchemaEntity(ctx);
    }

    @Override
    public Void visitShowProfileType(MySqlParser.ShowProfileTypeContext ctx) {
        return super.visitShowProfileType(ctx);
    }

    @Override
    public Void visitBinlogStatement(MySqlParser.BinlogStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.BINLOG.name());
        return super.visitBinlogStatement(ctx);
    }

    @Override
    public Void visitCacheIndexStatement(MySqlParser.CacheIndexStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CACHE_INDEX.name());
        return super.visitCacheIndexStatement(ctx);
    }


    @Override
    public Void visitFlushStatement(MySqlParser.FlushStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.FLUSH.name());
        return super.visitFlushStatement(ctx);
    }

    @Override
    public Void visitKillStatement(MySqlParser.KillStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.KILL.name());
        return super.visitKillStatement(ctx);
    }


    @Override
    public Void visitLoadIndexIntoCache(MySqlParser.LoadIndexIntoCacheContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LOAD_INDEX.name());
        return super.visitLoadIndexIntoCache(ctx);
    }

    @Override
    public Void visitResetStatement(MySqlParser.ResetStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RESET.name());
        return super.visitResetStatement(ctx);
    }

    @Override
    public Void visitShutdownStatement(MySqlParser.ShutdownStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SHUTDOWN.name());
        return super.visitShutdownStatement(ctx);
    }


    @Override
    public Void visitSimpleDescribeStatement(MySqlParser.SimpleDescribeStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DESCRIBE.name());
        return super.visitSimpleDescribeStatement(ctx);
    }


    @Override
    public Void visitFullDescribeStatement(MySqlParser.FullDescribeStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DESCRIBE_FULL.name());
        return super.visitFullDescribeStatement(ctx);
    }

    @Override
    public Void visitHelpStatement(MySqlParser.HelpStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.HELP.name());
        return super.visitHelpStatement(ctx);
    }

    @Override
    public Void visitUseStatement(MySqlParser.UseStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.USE.name());
        return super.visitUseStatement(ctx);
    }

    @Override
    public Void visitSignalStatement(MySqlParser.SignalStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SIGNAL.name());
        return super.visitSignalStatement(ctx);
    }

    @Override
    public Void visitResignalStatement(MySqlParser.ResignalStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.RESIGNAL.name());
        return super.visitResignalStatement(ctx);
    }

    @Override
    public Void visitDiagnosticsStatement(MySqlParser.DiagnosticsStatementContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DIAGNOSTICS.name());
        return super.visitDiagnosticsStatement(ctx);
    }
}
