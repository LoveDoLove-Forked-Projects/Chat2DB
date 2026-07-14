package ai.chat2db.community.web.api.model.response.db;

import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionTrace;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionTraceStep;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SqlCompletionResponseTest {

    @Test
    void publicCompletionResponseDoesNotSerializeParserTrace() throws Exception {
        ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse result =
                ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse.success(7, 10,
                List.of(SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, "select")));
        result.setTrace(new SqlCompletionTrace(List.of(new SqlCompletionTraceStep("c3", Map.of("available", true)))));

        String json = new ObjectMapper().writeValueAsString(SqlCompletionResponse.from(result));

        Assertions.assertFalse(json.contains("trace"));
        Assertions.assertTrue(json.contains("candidates"));
        Assertions.assertTrue(json.contains("select"));
    }
}
