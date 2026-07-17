package ai.chat2db.community.web.api.adapter.db.execution;

import ai.chat2db.community.tools.console.ConsoleOutboundRegistry;
import com.alibaba.fastjson2.JSON;

import java.util.concurrent.atomic.AtomicLong;

public class ConsoleSqlExecutionSink implements ISqlExecutionSink {

    public static final String ACTION_TYPE = "sql_execution_event";

    private final String requestUuid;
    private final String executionId;
    private final AtomicLong eventSequence = new AtomicLong();

    public ConsoleSqlExecutionSink(String requestUuid, String executionId) {
        this.requestUuid = requestUuid;
        this.executionId = executionId;
    }

    @Override
    public synchronized void send(String eventType, Object message, Integer statementSequence, Integer resultSequence) {
        String resultKey = buildResultKey(statementSequence, resultSequence);
        SqlExecutionEvent event = SqlExecutionEvent.builder()
                .executionId(executionId)
                .eventSequence(eventSequence.incrementAndGet())
                .occurredAtEpochMs(System.currentTimeMillis())
                .eventType(eventType)
                .statementSequence(statementSequence)
                .resultSequence(resultSequence)
                .resultKey(resultKey)
                .message(message)
                .build();
        SqlExecutionPushMessage pushMessage = SqlExecutionPushMessage.builder()
                .uuid(requestUuid)
                .actionType(ACTION_TYPE)
                .message(event)
                .build();
        ConsoleOutboundRegistry.send(JSON.toJSONString(pushMessage));
    }

    private String buildResultKey(Integer statementSequence, Integer resultSequence) {
        if (statementSequence == null) {
            return null;
        }
        return executionId + ":" + statementSequence + ":" + (resultSequence == null ? 0 : resultSequence);
    }
}
