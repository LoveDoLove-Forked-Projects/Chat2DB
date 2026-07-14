package ai.chat2db.spi.parser.completion;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalContext;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionTraceStep;
import ai.chat2db.community.domain.api.model.completion.evidence.SqlCompletionRuleEvidence;
import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlan;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public record SqlCompletionPipelineState(DbSqlCompletionRequest request,
                                         SqlCompletionInputCleanResponse input,
                                         SqlCompletionStatementWindow window,
                                         SqlCompletionCursorContext cursorContext,
                                         SqlCompletionDummySql dummySql,
                                         SqlCompletionLocalContext localContext,
                                         SqlCompletionCandidates c3Result,
                                         List<SqlCompletionRuleEvidence> ruleEvidence,
                                         List<SqlCompletionSlot> slots,
                                         List<SqlCompletionIntent> intents,
                                         SqlCompletionCandidatePlan candidatePlan,
                                         SqlCompletionResponse result,
                                         List<SqlCompletionTraceStep> traceSteps,
                                         Object dialectContext) {

    public SqlCompletionPipelineState {
        c3Result = c3Result == null ? SqlCompletionCandidates.unavailable() : c3Result;
        localContext = localContext == null ? SqlCompletionLocalContext.empty() : localContext;
        ruleEvidence = ruleEvidence == null ? List.of() : List.copyOf(ruleEvidence);
        slots = slots == null ? List.of() : List.copyOf(slots);
        intents = intents == null ? List.of() : List.copyOf(intents);
        candidatePlan = candidatePlan == null ? SqlCompletionCandidatePlan.empty() : candidatePlan;
        result = result == null ? SqlCompletionResponse.empty() : result;
        traceSteps = traceSteps == null ? List.of() : List.copyOf(traceSteps);
    }

    public static SqlCompletionPipelineState start(DbSqlCompletionRequest request) {
        return new SqlCompletionPipelineState(request, null, null, null, null, SqlCompletionLocalContext.empty(),
                SqlCompletionCandidates.unavailable(), List.of(), List.of(), List.of(), SqlCompletionCandidatePlan.empty(),
                SqlCompletionResponse.empty(), List.of(), null);
    }

    public SqlCompletionPipelineState withInput(SqlCompletionInputCleanResponse input) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withWindow(SqlCompletionStatementWindow window) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withCursorContext(SqlCompletionCursorContext cursorContext) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withDummySql(SqlCompletionDummySql dummySql) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withLocalContext(SqlCompletionLocalContext localContext) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withC3Result(SqlCompletionCandidates c3Result) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withRuleEvidence(List<SqlCompletionRuleEvidence> ruleEvidence) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withSlots(List<SqlCompletionSlot> slots) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withIntents(List<SqlCompletionIntent> intents) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withCandidatePlan(SqlCompletionCandidatePlan candidatePlan) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withResult(SqlCompletionResponse result) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withDialectContext(Object dialectContext) {
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, traceSteps, dialectContext);
    }

    public SqlCompletionPipelineState withTraceStep(String stage, Map<String, Object> values) {
        List<SqlCompletionTraceStep> nextTraceSteps = new ArrayList<>(traceSteps);
        nextTraceSteps.add(new SqlCompletionTraceStep(stage, values));
        return new SqlCompletionPipelineState(request, input, window, cursorContext, dummySql, localContext, c3Result,
                ruleEvidence, slots, intents, candidatePlan, result, nextTraceSteps, dialectContext);
    }

    @SuppressWarnings("unchecked")
    public <T> T dialectContext(Class<T> type) {
        if (type == null || dialectContext == null || !type.isInstance(dialectContext)) {
            return null;
        }
        return (T) dialectContext;
    }
}
