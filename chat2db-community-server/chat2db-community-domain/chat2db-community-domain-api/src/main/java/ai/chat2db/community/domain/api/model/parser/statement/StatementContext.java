package ai.chat2db.community.domain.api.model.parser.statement;

import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class StatementContext {

    private CommonTokenStream commonTokenStream;

    private Statement currentStatement;

    private IntervalSet recoverSet;

    private AbstractParseTreeVisitor visitor;


    private final List<Statement> statements = new ArrayList<>(100);


    public StatementContext() {
    }

    public StatementContext(CommonTokenStream commonTokenStream) {
        this.commonTokenStream = commonTokenStream;
    }

    public IntervalSet getRecoverSet() {
        return recoverSet;
    }

    public void setRecoverSet(IntervalSet recoverSet) {
        this.recoverSet = recoverSet;
    }

    public AbstractParseTreeVisitor getVisitor() {
        return visitor;
    }

    public void setVisitor(AbstractParseTreeVisitor visitor) {
        this.visitor = visitor;
    }

    public CommonTokenStream getCommonTokenStream() {
        return commonTokenStream;
    }


    public Statement getCurrentStatement() {
        return currentStatement;
    }

    public void setCurrentStatement(Statement currentStatement) {
        this.currentStatement = currentStatement;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public String getText(Interval interval) {
        return commonTokenStream.getText(interval);
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void setStatementType(String type) {
        currentStatement.setType(type);
    }

    public void setStatementType(SqlTypeEnum type) {
        currentStatement.setType(type.name());
    }
}
