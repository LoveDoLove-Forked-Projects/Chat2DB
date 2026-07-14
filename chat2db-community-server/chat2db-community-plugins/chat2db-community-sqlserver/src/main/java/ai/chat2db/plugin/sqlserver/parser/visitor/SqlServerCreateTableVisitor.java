package ai.chat2db.plugin.sqlserver.parser.visitor;

import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.plugin.sqlserver.parser.base.TSqlParserBaseVisitor;

public class SqlServerCreateTableVisitor extends TSqlParserBaseVisitor<Void> {

    private final StatementContext context;

    public SqlServerCreateTableVisitor(StatementContext context) {
        this.context = context;
    }

}
