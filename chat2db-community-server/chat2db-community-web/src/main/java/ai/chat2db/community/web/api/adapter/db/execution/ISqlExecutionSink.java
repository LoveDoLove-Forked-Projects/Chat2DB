package ai.chat2db.community.web.api.adapter.db.execution;

public interface ISqlExecutionSink {

    default void send(String eventType, Object message) {
        send(eventType, message, (SqlExecutionEventIdentity) null);
    }

    default void send(String eventType, Object message, SqlExecutionEventIdentity identity) {
        Integer statementSequence = identity == null ? null : identity.getStatementSequence();
        Integer resultSequence = identity == null ? null : identity.getResultSequence();
        send(eventType, message, statementSequence, resultSequence);
    }

    void send(String eventType, Object message, Integer statementSequence, Integer resultSequence);
}
