package ai.chat2db.spi.parser.completion;

import ai.chat2db.spi.ISqlCompletionDialect;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;


public final class SqlCompletionPipeline {

    private final SqlCompletionDialectComponents components;

    public SqlCompletionPipeline(ISqlCompletionDialect dialect) {
        this(dialect.components());
    }

    public SqlCompletionPipeline(SqlCompletionDialectComponents components) {
        this.components = components;
    }

    public SqlCompletionResponse complete(DbSqlCompletionRequest request) {
        SqlCompletionPipelineState state = SqlCompletionPipelineState.start(request);
        state = state.withTraceStep("request", SqlCompletionTraceSupport.request(state));
        state = state.withInput(components.inputCleaner().clean(state));
        state = state.withTraceStep("input", SqlCompletionTraceSupport.input(state.input()));
        state = state.withWindow(components.statementLocator().locate(state));
        state = state.withTraceStep("statementWindow", SqlCompletionTraceSupport.window(state.window()));
        state = state.withCursorContext(components.cursorAnalyzer().analyze(state));
        state = state.withTraceStep("cursor", SqlCompletionTraceSupport.cursor(state.cursorContext()));
        if (state.cursorContext() == null) {
            state = state.withResult(SqlCompletionResponse.empty());
            state = state.withTraceStep("result", SqlCompletionTraceSupport.result(state));
            state.result().setTrace(SqlCompletionTraceSupport.trace(state));
            return state.result();
        }
        state = state.withDummySql(components.dummyBuilder().build(state));
        state = state.withTraceStep("dummySql", SqlCompletionTraceSupport.dummy(state.dummySql()));
        state = state.withLocalContext(components.localContextCollector().collect(state));
        state = state.withTraceStep("localContext", SqlCompletionTraceSupport.localContext(state.localContext()));
        state = state.withC3Result(components.c3Collector().collect(state));
        state = state.withTraceStep("c3", SqlCompletionTraceSupport.c3(state.c3Result()));
        state = state.withRuleEvidence(components.ruleEvidenceCollector().collect(state));
        state = state.withTraceStep("ruleEvidence", SqlCompletionTraceSupport.evidence(state.ruleEvidence()));
        state = state.withSlots(components.slotClassifier().classify(state));
        state = state.withTraceStep("slots", SqlCompletionTraceSupport.slots(state.slots()));
        state = state.withIntents(components.intentResolver().resolve(state));
        state = state.withTraceStep("intents", SqlCompletionTraceSupport.intents(state.intents()));
        state = state.withCandidatePlan(components.candidatePlanner().plan(state));
        state = state.withTraceStep("candidatePlan", SqlCompletionTraceSupport.candidatePlan(state.candidatePlan()));
        state = state.withResult(components.presentationProcessor().process(state));
        state = state.withTraceStep("result", SqlCompletionTraceSupport.result(state));
        state.result().setTrace(SqlCompletionTraceSupport.trace(state));
        return state.result();
    }
}
