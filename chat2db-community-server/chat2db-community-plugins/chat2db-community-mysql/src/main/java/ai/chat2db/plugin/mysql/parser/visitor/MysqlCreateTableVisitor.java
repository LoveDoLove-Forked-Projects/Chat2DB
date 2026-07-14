package ai.chat2db.plugin.mysql.parser.visitor;

import ai.chat2db.community.domain.api.enums.parser.IdentifierTypeEnum;
import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.community.domain.api.model.parser.statement.create.CreateTableStatement;
import ai.chat2db.community.domain.api.model.parser.token.Column;
import ai.chat2db.community.domain.api.model.parser.token.ColumnDeclaration;
import ai.chat2db.mysql.parser.base.MySqlParser;
import ai.chat2db.mysql.parser.base.MySqlParserBaseVisitor;
import ai.chat2db.plugin.mysql.parser.util.MysqlStringUtil;
import io.vavr.control.Try;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class MysqlCreateTableVisitor extends MySqlParserBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(MysqlCreateTableVisitor.class);

    private final StatementContext context;


    public MysqlCreateTableVisitor(StatementContext context) {
        this.context = context;
    }

    @Override
    public Void visitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        Try.of(() -> {
            CreateTableStatement statement = new CreateTableStatement();
            context.setCurrentStatement(statement);
            statement.setType(SqlTypeEnum.CREATE_TABLE.name());
            statement.setSql(context.getText(ctx.getSourceInterval()));
            MySqlParser.TableNameContext tableNameContext = ctx.tableDeclarationName().tableName();
            if (Objects.isNull(tableNameContext)) {
                context.addStatement(statement);
                return null;
            }

            MySqlParser.FullIdContext fullIdContext = tableNameContext.fullId();
            if (Objects.isNull(fullIdContext)) {
                context.addStatement(statement);
                return null;
            }
            List<MySqlParser.UidContext> uids = fullIdContext.uid();
            if (CollectionUtils.isNotEmpty(uids)) {
                String databaseName;
                String tableName = MysqlStringUtil.removeQuote(uids.get(uids.size() - 1).getText());
                if (uids.size() == 2) {
                    databaseName = MysqlStringUtil.removeQuote(uids.get(0).getText());
                    statement.setDatabaseName(databaseName);
                    context.getCurrentStatement().addIdentifier(databaseName, IdentifierTypeEnum.DATABASE.name(), uids.get(0).start);
                }

                statement.setTableName(tableName);
                context.getCurrentStatement().addIdentifier(tableName, IdentifierTypeEnum.TABLE.name(), uids.get(uids.size() - 1).start);
            }
            context.addStatement(statement);
            return null;
        }).onFailure(e -> log.error(" mysql visitColumnCreateTable error", e));
        return super.visitColumnCreateTable(ctx);
    }


    @Override
    public Void visitColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        Try.of(() -> {
            CreateTableStatement CreateTableStatement = (CreateTableStatement) context.getCurrentStatement();
            MySqlParser.FullColumnNameContext fullColumnNameContext = ctx.columnDeclarationName().fullColumnName();
            if (Objects.isNull(fullColumnNameContext)) {
                return null;
            }
            ColumnDeclaration columnDeclaration = new ColumnDeclaration();
            Column column = new Column();
            column.setColumnName(MysqlStringUtil.removeQuote(fullColumnNameContext.getText()));
            MySqlParser.ColumnDefinitionContext columnDefinitionContext = ctx.columnDefinition();
            if (Objects.isNull(columnDefinitionContext)) {
                return null;
            }
            MySqlParser.DataTypeContext dataTypeContext = columnDefinitionContext.dataType();
            if (Objects.nonNull(dataTypeContext)) {
                column.setDisplayColumnType(MysqlStringUtil.removeQuote(dataTypeContext.getText()));
            }
            columnDeclaration.setColumn(column);
            CreateTableStatement.addColumnDeclaration(columnDeclaration);
            return null;
        }).onFailure(e -> log.error(" mysql visitColumnDeclaration error", e));
        return null;
    }


    @Override
    public Void visitForeignKeyTableConstraint(MySqlParser.ForeignKeyTableConstraintContext ctx) {
        return super.visitForeignKeyTableConstraint(ctx);
    }

    @Override
    public Void visitCheckTableConstraint(MySqlParser.CheckTableConstraintContext ctx) {
        return super.visitCheckTableConstraint(ctx);
    }

    @Override
    public Void visitPrimaryKeyTableConstraint(MySqlParser.PrimaryKeyTableConstraintContext ctx) {
        return super.visitPrimaryKeyTableConstraint(ctx);
    }


    @Override
    public Void visitUniqueKeyTableConstraint(MySqlParser.UniqueKeyTableConstraintContext ctx) {
        return super.visitUniqueKeyTableConstraint(ctx);
    }


    @Override
    public Void visitTableOptionComment(MySqlParser.TableOptionCommentContext ctx) {
        TerminalNode comment = ctx.STRING_LITERAL();
        if (Objects.nonNull(comment)) {
            CreateTableStatement statement = (CreateTableStatement) context.getCurrentStatement();
            statement.setTableComment(comment.getText().substring(1, comment.getText().length() - 1));
        }
        return null;
    }
}
