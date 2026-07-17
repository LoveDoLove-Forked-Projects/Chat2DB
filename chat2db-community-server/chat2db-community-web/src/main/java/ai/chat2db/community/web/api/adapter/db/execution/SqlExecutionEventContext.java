package ai.chat2db.community.web.api.adapter.db.execution;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class SqlExecutionEventContext {

    private static final String STREAM_RESULT_ID = "streamResultId";

    private final AtomicInteger statementSequence = new AtomicInteger();
    private final AtomicInteger fallbackResultSequence = new AtomicInteger();

    private Integer currentStatementSequence;
    private Integer currentResultSequence;

    synchronized SqlExecutionEventIdentity statementStarted() {
        currentStatementSequence = statementSequence.incrementAndGet();
        currentResultSequence = null;
        return snapshot();
    }

    synchronized SqlExecutionEventIdentity resultActive(ExecuteResponse result) {
        currentResultSequence = resolveResultSequence(result);
        return snapshot();
    }

    synchronized SqlExecutionEventIdentity statementFinished() {
        return snapshot();
    }

    synchronized SqlExecutionEventIdentity currentIdentity() {
        return snapshot();
    }

    private SqlExecutionEventIdentity snapshot() {
        return new SqlExecutionEventIdentity(currentStatementSequence, currentResultSequence);
    }

    private Integer resolveResultSequence(ExecuteResponse result) {
        Integer streamResultId = getStreamResultId(result);
        if (streamResultId != null) {
            return streamResultId;
        }
        if (result != null && result.getResultSetId() != null) {
            return result.getResultSetId();
        }
        if (currentResultSequence != null) {
            return currentResultSequence;
        }
        return fallbackResultSequence.incrementAndGet();
    }

    private Integer getStreamResultId(ExecuteResponse result) {
        if (result == null) {
            return null;
        }
        Map<String, Object> extra = result.getExtra();
        if (extra == null) {
            return null;
        }
        Object value = extra.get(STREAM_RESULT_ID);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
