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
import ai.chat2db.plugin.postgresql.parser.listener.PgsqlSelectListener;
import ai.chat2db.spi.util.SqlStringUtil;
import ai.chat2db.spi.util.TokenUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.List;
import java.util.Objects;

@Slf4j
public class PgsqlValidTableVisitor extends PostgreSQLParserBaseVisitor<Void> {


    private final StatementContext context;
    private final PgsqlSelectListener pgsqlSelectListener;

    public PgsqlValidTableVisitor(StatementContext context) {
        this.context = context;
        pgsqlSelectListener = new PgsqlSelectListener(context);
    }

    @Override
    public Void visitStmtmulti(PostgreSQLParser.StmtmultiContext ctx) {
        List<PostgreSQLParser.StmtContext> stmt = ctx.stmt();
        TokenStream commonTokenStream = context.getCommonTokenStream();
        for (PostgreSQLParser.StmtContext child : stmt) {
            Token start = child.getStart();
            Token stop = child.getStop();
            if (start.getType() == PostgreSQLLexer.MetaCommand) {
                continue;
            }
            Statement statement = new Statement();
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
                identifier.setIdentifierAlias(optAliasClauseContext.getText());
                identifier.setLastToken(optAliasClauseContext.getStop());
            }
            currentStatement.addIdentifier(identifier);
        }
    }

    @Override
    public Void visitAltereventtrigstmt(PostgreSQLParser.AltereventtrigstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_EVENT.name());
        return super.visitAltereventtrigstmt(ctx);
    }

    @Override
    public Void visitAltercollationstmt(PostgreSQLParser.AltercollationstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_COLLATION.name());
        return super.visitAltercollationstmt(ctx);
    }

    @Override
    public Void visitAlterextensionstmt(PostgreSQLParser.AlterextensionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_EXTENSION.name());
        return super.visitAlterextensionstmt(ctx);
    }

    @Override
    public Void visitAlterextensioncontentsstmt(PostgreSQLParser.AlterextensioncontentsstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_EXTENSION_CONTENTS.name());
        return super.visitAlterextensioncontentsstmt(ctx);
    }

    @Override
    public Void visitAlterfdwstmt(PostgreSQLParser.AlterfdwstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_FDW.name());
        return super.visitAlterfdwstmt(ctx);
    }

    @Override
    public Void visitAlterforeignserverstmt(PostgreSQLParser.AlterforeignserverstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_FOREIGN_SERVER.name());
        return super.visitAlterforeignserverstmt(ctx);
    }

    @Override
    public Void visitAltergroupstmt(PostgreSQLParser.AltergroupstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_GROUP.name());
        return super.visitAltergroupstmt(ctx);
    }

    @Override
    public Void visitAlterobjectdependsstmt(PostgreSQLParser.AlterobjectdependsstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_OBJECT_DEPENDS.name());
        return super.visitAlterobjectdependsstmt(ctx);
    }

    @Override
    public Void visitAlterobjectschemastmt(PostgreSQLParser.AlterobjectschemastmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_OBJECT_SCHEMA.name());
        return super.visitAlterobjectschemastmt(ctx);
    }

    @Override
    public Void visitAlterownerstmt(PostgreSQLParser.AlterownerstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_OWNER.name());
        return super.visitAlterownerstmt(ctx);
    }

    @Override
    public Void visitAlterpolicystmt(PostgreSQLParser.AlterpolicystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_POLICY.name());
        return super.visitAlterpolicystmt(ctx);
    }

    @Override
    public Void visitAlterpublicationstmt(PostgreSQLParser.AlterpublicationstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_PUBLICATION.name());
        return super.visitAlterpublicationstmt(ctx);
    }

    @Override
    public Void visitAlterstatsstmt(PostgreSQLParser.AlterstatsstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_STATISTICS.name());
        return super.visitAlterstatsstmt(ctx);
    }

    @Override
    public Void visitAltersubscriptionstmt(PostgreSQLParser.AltersubscriptionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_SUBSCRIPTION.name());
        return super.visitAltersubscriptionstmt(ctx);
    }

    @Override
    public Void visitAltersystemstmt(PostgreSQLParser.AltersystemstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_SYSTEM.name());
        return super.visitAltersystemstmt(ctx);
    }

    @Override
    public Void visitAltertsconfigurationstmt(PostgreSQLParser.AltertsconfigurationstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_TSCONFIGURATION.name());
        return super.visitAltertsconfigurationstmt(ctx);
    }

    @Override
    public Void visitAltertsdictionarystmt(PostgreSQLParser.AltertsdictionarystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_TSDICTIONARY.name());
        return super.visitAltertsdictionarystmt(ctx);
    }

    @Override
    public Void visitAlterusermappingstmt(PostgreSQLParser.AlterusermappingstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ALTER_USER_MAPPING.name());
        return super.visitAlterusermappingstmt(ctx);
    }

    @Override
    public Void visitAnalyzestmt(PostgreSQLParser.AnalyzestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.ANALYZE.name());
        return super.visitAnalyzestmt(ctx);
    }

    @Override
    public Void visitCheckpointstmt(PostgreSQLParser.CheckpointstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CHECKPOINT.name());
        return super.visitCheckpointstmt(ctx);
    }

    @Override
    public Void visitCloseportalstmt(PostgreSQLParser.CloseportalstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CLOSE_PORTAL.name());
        return super.visitCloseportalstmt(ctx);
    }

    @Override
    public Void visitClusterstmt(PostgreSQLParser.ClusterstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CLUSTER.name());
        return super.visitClusterstmt(ctx);
    }

    @Override
    public Void visitCommentstmt(PostgreSQLParser.CommentstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.COMMENT.name());
        return super.visitCommentstmt(ctx);
    }

    @Override
    public Void visitConstraintssetstmt(PostgreSQLParser.ConstraintssetstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CONSTRAINTS_SET.name());
        return super.visitConstraintssetstmt(ctx);
    }

    @Override
    public Void visitCopystmt(PostgreSQLParser.CopystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.COPY.name());
        return super.visitCopystmt(ctx);
    }

    @Override
    public Void visitCreateamstmt(PostgreSQLParser.CreateamstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_ACCESS_METHOD.name());
        return super.visitCreateamstmt(ctx);
    }

    @Override
    public Void visitCreatecaststmt(PostgreSQLParser.CreatecaststmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_CAST.name());
        return super.visitCreatecaststmt(ctx);
    }

    @Override
    public Void visitCreateconversionstmt(PostgreSQLParser.CreateconversionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_CONVERSION.name());
        return super.visitCreateconversionstmt(ctx);
    }

    @Override
    public Void visitCreatedomainstmt(PostgreSQLParser.CreatedomainstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_DOMAIN.name());
        return super.visitCreatedomainstmt(ctx);
    }

    @Override
    public Void visitCreateextensionstmt(PostgreSQLParser.CreateextensionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_EXTENSION.name());
        return super.visitCreateextensionstmt(ctx);
    }

    @Override
    public Void visitCreatefdwstmt(PostgreSQLParser.CreatefdwstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FDW.name());
        return super.visitCreatefdwstmt(ctx);
    }

    @Override
    public Void visitCreateforeignserverstmt(PostgreSQLParser.CreateforeignserverstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FOREIGN_SERVER.name());
        return super.visitCreateforeignserverstmt(ctx);
    }

    @Override
    public Void visitCreateforeigntablestmt(PostgreSQLParser.CreateforeigntablestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_FOREIGN_TABLE.name());
        return super.visitCreateforeigntablestmt(ctx);
    }

    @Override
    public Void visitCreategroupstmt(PostgreSQLParser.CreategroupstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_GROUP.name());
        return super.visitCreategroupstmt(ctx);
    }

    @Override
    public Void visitCreatematviewstmt(PostgreSQLParser.CreatematviewstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_MATERIALIZED_VIEW.name());
        return super.visitCreatematviewstmt(ctx);
    }

    @Override
    public Void visitCreateopclassstmt(PostgreSQLParser.CreateopclassstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_OPCLASS.name());
        return super.visitCreateopclassstmt(ctx);
    }

    @Override
    public Void visitCreateopfamilystmt(PostgreSQLParser.CreateopfamilystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_OPFAMILY.name());
        return super.visitCreateopfamilystmt(ctx);
    }

    @Override
    public Void visitCreatepolicystmt(PostgreSQLParser.CreatepolicystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_POLICY.name());
        return super.visitCreatepolicystmt(ctx);
    }

    @Override
    public Void visitCreatepublicationstmt(PostgreSQLParser.CreatepublicationstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_PUBLICATION.name());
        return super.visitCreatepublicationstmt(ctx);
    }

    @Override
    public Void visitCreatestatsstmt(PostgreSQLParser.CreatestatsstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_STATISTICS.name());
        return super.visitCreatestatsstmt(ctx);
    }

    @Override
    public Void visitCreatesubscriptionstmt(PostgreSQLParser.CreatesubscriptionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_SUBSCRIPTION.name());
        return super.visitCreatesubscriptionstmt(ctx);
    }

    @Override
    public Void visitCreatetransformstmt(PostgreSQLParser.CreatetransformstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TRANSFORM.name());
        return super.visitCreatetransformstmt(ctx);
    }

    @Override
    public Void visitCreatestmt(PostgreSQLParser.CreatestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_TABLE.name());
        return null;
    }

    @Override
    public Void visitCreateeventtrigstmt(PostgreSQLParser.CreateeventtrigstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.CREATE_EVENT_TRIGGER.name());
        return super.visitCreateeventtrigstmt(ctx);
    }

    @Override
    public Void visitDeallocatestmt(PostgreSQLParser.DeallocatestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DEALLOCATE.name());
        return super.visitDeallocatestmt(ctx);
    }

    @Override
    public Void visitDeclarecursorstmt(PostgreSQLParser.DeclarecursorstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DECLARE_CURSOR.name());
        return super.visitDeclarecursorstmt(ctx);
    }

    @Override
    public Void visitDefinestmt(PostgreSQLParser.DefinestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DEFINE.name());
        return super.visitDefinestmt(ctx);
    }

    @Override
    public Void visitDiscardstmt(PostgreSQLParser.DiscardstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DISCARD.name());
        return super.visitDiscardstmt(ctx);
    }

    @Override
    public Void visitDostmt(PostgreSQLParser.DostmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DO.name());
        return super.visitDostmt(ctx);
    }

    @Override
    public Void visitDropcaststmt(PostgreSQLParser.DropcaststmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_CAST.name());
        return super.visitDropcaststmt(ctx);
    }

    @Override
    public Void visitDropopclassstmt(PostgreSQLParser.DropopclassstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_OPCLASS.name());
        return super.visitDropopclassstmt(ctx);
    }

    @Override
    public Void visitDropopfamilystmt(PostgreSQLParser.DropopfamilystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_OPFAMILY.name());
        return super.visitDropopfamilystmt(ctx);
    }

    @Override
    public Void visitDropownedstmt(PostgreSQLParser.DropownedstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_OWNED.name());
        return super.visitDropownedstmt(ctx);
    }

    @Override
    public Void visitDropsubscriptionstmt(PostgreSQLParser.DropsubscriptionstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_SUBSCRIPTION.name());
        return super.visitDropsubscriptionstmt(ctx);
    }

    @Override
    public Void visitDroptransformstmt(PostgreSQLParser.DroptransformstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_TRANSFORM.name());
        return super.visitDroptransformstmt(ctx);
    }

    @Override
    public Void visitDropusermappingstmt(PostgreSQLParser.DropusermappingstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.DROP_USER_MAPPING.name());
        return super.visitDropusermappingstmt(ctx);
    }

    @Override
    public Void visitExecutestmt(PostgreSQLParser.ExecutestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.EXECUTE.name());
        return super.visitExecutestmt(ctx);
    }

    @Override
    public Void visitFetchstmt(PostgreSQLParser.FetchstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.FETCH.name());
        return super.visitFetchstmt(ctx);
    }

    @Override
    public Void visitGrantrolestmt(PostgreSQLParser.GrantrolestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.GRANT_ROLE.name());
        return super.visitGrantrolestmt(ctx);
    }

    @Override
    public Void visitImportforeignschemastmt(PostgreSQLParser.ImportforeignschemastmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.IMPORT_FOREIGN_SCHEMA.name());
        return super.visitImportforeignschemastmt(ctx);
    }

    @Override
    public Void visitListenstmt(PostgreSQLParser.ListenstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LISTEN.name());
        return super.visitListenstmt(ctx);
    }

    @Override
    public Void visitLoadstmt(PostgreSQLParser.LoadstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LOAD.name());
        return super.visitLoadstmt(ctx);
    }

    @Override
    public Void visitLockstmt(PostgreSQLParser.LockstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.LOCK.name());
        return super.visitLockstmt(ctx);
    }

    @Override
    public Void visitNotifystmt(PostgreSQLParser.NotifystmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.NOTIFY.name());
        return super.visitNotifystmt(ctx);
    }

    @Override
    public Void visitReassignownedstmt(PostgreSQLParser.ReassignownedstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REASSIGN_OWNED.name());
        return super.visitReassignownedstmt(ctx);
    }

    @Override
    public Void visitRefreshmatviewstmt(PostgreSQLParser.RefreshmatviewstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REFRESH_MATERIALIZED_VIEW.name());
        return super.visitRefreshmatviewstmt(ctx);
    }

    @Override
    public Void visitReindexstmt(PostgreSQLParser.ReindexstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REINDEX.name());
        return super.visitReindexstmt(ctx);
    }

    @Override
    public Void visitRemoveaggrstmt(PostgreSQLParser.RemoveaggrstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REMOVE_AGGREGATE.name());
        return super.visitRemoveaggrstmt(ctx);
    }

    @Override
    public Void visitRemovefuncstmt(PostgreSQLParser.RemovefuncstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REMOVE_FUNCTION.name());
        return super.visitRemovefuncstmt(ctx);
    }

    @Override
    public Void visitRemoveoperstmt(PostgreSQLParser.RemoveoperstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REMOVE_OPERATOR.name());
        return super.visitRemoveoperstmt(ctx);
    }

    @Override
    public Void visitRevokerolestmt(PostgreSQLParser.RevokerolestmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.REVOKE_ROLE.name());
        return super.visitRevokerolestmt(ctx);
    }

    @Override
    public Void visitSeclabelstmt(PostgreSQLParser.SeclabelstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SECURITY_LABEL.name());
        return super.visitSeclabelstmt(ctx);
    }

    @Override
    public Void visitUnlistenstmt(PostgreSQLParser.UnlistenstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.UNLISTEN.name());
        return super.visitUnlistenstmt(ctx);
    }

    @Override
    public Void visitVacuumstmt(PostgreSQLParser.VacuumstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.VACUUM.name());
        return super.visitVacuumstmt(ctx);
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
    public Void visitInsert_target(PostgreSQLParser.Insert_targetContext ctx) {
        splitName(IdentifierTypeEnum.TABLE, ctx);
        return super.visitInsert_target(ctx);
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
    public Void visitSelectstmt(PostgreSQLParser.SelectstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.SELECT.name());
        pgsqlSelectListener.parserSelectStatement(ctx);
        return null;
    }

    @Override
    public Void visitInsertstmt(PostgreSQLParser.InsertstmtContext ctx) {
        Statement currentStatement = context.getCurrentStatement();
        if (Objects.isNull(currentStatement)) {
            return null;
        }
        currentStatement.setType(SqlTypeEnum.INSERT.name());
        return super.visitInsertstmt(ctx);
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

    private void visitTableName(PostgreSQLParser.Relation_expr_opt_aliasContext ctx) {
        PostgreSQLParser.Relation_exprContext relationExprContext = ctx.relation_expr();
        if (Objects.nonNull(relationExprContext)) {
            splitName(IdentifierTypeEnum.TABLE, relationExprContext);
        }

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

}
