package ai.chat2db.plugin.mysql.completion.context;

import ai.chat2db.plugin.mysql.completion.analysis.statement.dml.insert.MysqlInsertStatementAnalyzer;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionInsertStatementContext;
import ai.chat2db.plugin.mysql.completion.slot.MysqlSqlCompletionRuleSlot;
import ai.chat2db.spi.parser.completion.SqlCompletionPipelineState;

public final class MysqlSqlCompletionCandidateContextFactory {

    public MysqlSqlCompletionCandidateContext create(SqlCompletionPipelineState state) {
        MysqlSqlCompletionInsertStatementContext insertStatementContext =
                new MysqlInsertStatementAnalyzer().analyze(state.window(), state.cursorContext());
        MysqlSqlCompletionCandidateContext baseContext = new MysqlSqlCompletionCandidateContext(state.request(),
                state.input(), state.window(), state.dummySql(), state.cursorContext(), state.localContext(),
                insertStatementContext);
        MysqlSqlCompletionRuleSlot ruleSlot = MysqlSqlCompletionRuleSlot.classify(baseContext,
                state.c3Result());
        return new MysqlSqlCompletionCandidateContext(state.request(), state.input(), state.window(), state.dummySql(),
                state.cursorContext(), state.localContext(), insertStatementContext, state.c3Result(), ruleSlot);
    }
}
