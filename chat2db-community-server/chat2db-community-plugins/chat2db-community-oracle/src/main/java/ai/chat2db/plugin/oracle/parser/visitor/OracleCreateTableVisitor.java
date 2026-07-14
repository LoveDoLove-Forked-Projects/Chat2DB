package ai.chat2db.plugin.oracle.parser.visitor;

import ai.chat2db.community.domain.api.model.parser.statement.StatementContext;
import ai.chat2db.plugin.oracle.parser.base.PlSqlParserBaseVisitor;

public class OracleCreateTableVisitor extends PlSqlParserBaseVisitor<Void> {

    private StatementContext context;

    public OracleCreateTableVisitor(StatementContext context) {
        this.context = context;
    }
}
