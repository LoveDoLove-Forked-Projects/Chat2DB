package ai.chat2db.plugin.postgresql.parser.visitor;

import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParserBaseVisitor;

public class PgsqlCreateTableVisitor extends PostgreSQLParserBaseVisitor<Void> {

    private final StatementContext context;

    public PgsqlCreateTableVisitor(StatementContext context) {
        this.context = context;
    }
}
