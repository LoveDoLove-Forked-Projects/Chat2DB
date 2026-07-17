package ai.chat2db.community.web.api.adapter.db.execution;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlExecutionEventContextTest {

    @Test
    void failedResultAfterStatementFinishedKeepsActiveResultIdentity() {
        SqlExecutionEventContext context = new SqlExecutionEventContext();
        context.statementStarted();
        context.resultActive(ExecuteResponse.builder()
                .extra(Map.<String, Object>of("streamResultId", 7))
                .build());

        SqlExecutionEventIdentity statementFinished = context.statementFinished();
        SqlExecutionEventIdentity failedResult = context.resultActive(ExecuteResponse.builder()
                .success(Boolean.FALSE)
                .build());

        assertEquals(1, statementFinished.getStatementSequence());
        assertEquals(7, statementFinished.getResultSequence());
        assertEquals(1, failedResult.getStatementSequence());
        assertEquals(7, failedResult.getResultSequence());
    }
}
