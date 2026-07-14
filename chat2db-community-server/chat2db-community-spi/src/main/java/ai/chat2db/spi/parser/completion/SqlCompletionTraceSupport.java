package ai.chat2db.spi.parser.completion;

import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalContext;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalRelation;
import ai.chat2db.community.domain.api.model.completion.context.SqlCompletionLocalVariable;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionInputCleanResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionTrace;
import ai.chat2db.community.domain.api.model.completion.evidence.SqlCompletionRuleEvidence;
import ai.chat2db.community.domain.api.model.completion.intent.SqlCompletionIntent;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCursorContext;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionDummySql;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionStatementWindow;
import ai.chat2db.community.domain.api.model.completion.plan.SqlCompletionCandidatePlan;
import ai.chat2db.community.domain.api.model.completion.slot.SqlCompletionSlot;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class SqlCompletionTraceSupport {

    private SqlCompletionTraceSupport() {
    }

    static SqlCompletionTrace trace(SqlCompletionPipelineState state) {
        if (state == null) {
            return SqlCompletionTrace.empty();
        }
        return new SqlCompletionTrace(state.traceSteps());
    }

    static Map<String, Object> request(SqlCompletionPipelineState state) {
        return Map.of(
                "databaseType", Objects.toString(state.request().databaseType(), ""),
                "cursor", state.request().cursor(),
                "sqlLength", state.request().sql().length(),
                "minPrefixLength", state.request().minPrefixLength(),
                "keywordCase", state.request().keywordCase());
    }

    static Map<String, Object> input(SqlCompletionInputCleanResponse input) {
        if (input == null) {
            return Map.of();
        }
        return Map.of(
                "cursor", input.cursor(),
                "sourceLength", input.sourceSql().length(),
                "parseLength", input.parseSql().length(),
                "changed", !input.sourceSql().equals(input.parseSql()));
    }

    static Map<String, Object> window(SqlCompletionStatementWindow window) {
        if (window == null) {
            return Map.of();
        }
        return Map.of(
                "type", window.type(),
                "cursor", window.cursor(),
                "sourceCursor", window.sourceCursor(),
                "sourceStartOffset", window.sourceStartOffset(),
                "sourceEndOffset", window.sourceEndOffset(),
                "sourceLength", window.sourceSql().length(),
                "parseLength", window.parseSql().length());
    }

    static Map<String, Object> cursor(SqlCompletionCursorContext cursorContext) {
        if (cursorContext == null) {
            return Map.of();
        }
        return Map.of(
                "admitted", cursorContext.admitted(),
                "rejectReason", cursorContext.rejectReason(),
                "prefix", cursorContext.prefix(),
                "replaceStart", cursorContext.replaceStart(),
                "replaceEnd", cursorContext.replaceEnd(),
                "dotScoped", cursorContext.dotScoped(),
                "scope", cursorContext.scope());
    }

    static Map<String, Object> dummy(SqlCompletionDummySql dummySql) {
        if (dummySql == null) {
            return Map.of();
        }
        return Map.of(
                "type", dummySql.type(),
                "cursor", dummySql.cursor(),
                "insertedOffset", dummySql.insertedOffset(),
                "insertedLength", dummySql.insertedLength(),
                "sqlLength", dummySql.sql().length());
    }

    static Map<String, Object> c3(SqlCompletionCandidates c3Result) {
        if (c3Result == null) {
            return Map.of();
        }
        return Map.of(
                "available", c3Result.available(),
                "tokenIndex", c3Result.tokenIndex(),
                "tokens", c3Result.tokens(),
                "rules", c3Result.rules());
    }

    static Map<String, Object> localContext(SqlCompletionLocalContext localContext) {
        if (localContext == null) {
            return Map.of();
        }
        return Map.of(
                "relationCount", localContext.relations().size(),
                "variableCount", localContext.variables().size(),
                "relationSources", sourceCounts(localContext.relations().stream()
                        .map(SqlCompletionLocalRelation::sourceType)
                        .toList()),
                "variableSources", sourceCounts(localContext.variables().stream()
                        .map(SqlCompletionLocalVariable::sourceType)
                        .toList()));
    }

    private static Map<String, Long> sourceCounts(List<String> sourceTypes) {
        return sourceTypes.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(sourceType -> sourceType, Collectors.counting()));
    }

    static Map<String, Object> evidence(List<SqlCompletionRuleEvidence> ruleEvidence) {
        return Map.of("items", ruleEvidence == null ? List.of() : ruleEvidence);
    }

    static Map<String, Object> slots(List<SqlCompletionSlot> slots) {
        return Map.of("items", slots == null ? List.of() : slots);
    }

    static Map<String, Object> intents(List<SqlCompletionIntent> intents) {
        return Map.of("items", intents == null ? List.of() : intents);
    }

    static Map<String, Object> candidatePlan(SqlCompletionCandidatePlan candidatePlan) {
        return Map.of("items", candidatePlan == null ? List.of() : candidatePlan.items());
    }

    static Map<String, Object> result(SqlCompletionPipelineState state) {
        if (state == null || state.result() == null) {
            return Map.of();
        }
        return Map.of(
                "status", Objects.toString(state.result().getStatus(), ""),
                "replaceStart", state.result().getReplaceStart(),
                "replaceEnd", state.result().getReplaceEnd(),
                "candidateCount", state.result().getCandidates() == null ? 0 : state.result().getCandidates().size(),
                "editorHintCount", state.result().getEditorHints() == null ? 0 : state.result().getEditorHints().size(),
                "reasonCode", Objects.toString(state.result().getReasonCode(), ""));
    }
}
