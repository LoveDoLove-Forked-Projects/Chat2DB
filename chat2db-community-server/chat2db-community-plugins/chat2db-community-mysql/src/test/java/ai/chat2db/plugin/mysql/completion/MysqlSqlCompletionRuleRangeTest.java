package ai.chat2db.plugin.mysql.completion;

import ai.chat2db.community.domain.api.service.db.ISqlCompletionMetadataProvider;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionMetadataRequest;
import ai.chat2db.community.domain.api.model.completion.request.DbSqlCompletionRequest;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionMetadataResponse;
import ai.chat2db.community.domain.api.model.completion.result.SqlCompletionResponse;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MysqlSqlCompletionRuleRangeTest {

    private final MysqlSqlCompletionProvider provider = new MysqlSqlCompletionProvider();
    private final RecordingMetadataProvider metadataProvider = new RecordingMetadataProvider();

    @Test
    void triggerForEachRowPrefixDoesNotReuseCompletedTargetTableRule() {
        CompletionRun run = completeAtCaret("CREATE TRIGGER dsdasd\n"
                + "BEFORE INSERT\n"
                + "ON user_document\n"
                + "fo{caret}");

        Assertions.assertTrue(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.KEYWORD
                                && "FOR".equals(candidate.getLabel())),
                () -> "Missing FOR keyword in " + labels(run.result()));
        Assertions.assertFalse(metadataProvider.requests.stream()
                        .anyMatch(request -> SqlCompletionCandidateTypeEnum.TABLE.name().equals(request.type())
                                || SqlCompletionCandidateTypeEnum.TABLE_VIEW.name().equals(request.type())),
                () -> "Unexpected table metadata request in " + metadataProvider.requests);
        Assertions.assertFalse(run.result().getCandidates().stream()
                        .anyMatch(candidate -> candidate.getType() == SqlCompletionCandidateTypeEnum.TABLE
                                || candidate.getType() == SqlCompletionCandidateTypeEnum.TABLE_VIEW),
                () -> "Unexpected table candidate in " + labels(run.result()));
    }

    private CompletionRun completeAtCaret(String sql) {
        int caret = sql.indexOf("{caret}");
        Assertions.assertTrue(caret >= 0, "scenario must include {caret}");
        String sourceSql = sql.replace("{caret}", "");
        SqlCompletionResponse result = provider.complete(DbSqlCompletionRequest.of(
                sourceSql, caret, "MYSQL", 1, metadataProvider));
        return new CompletionRun(sourceSql, caret, result);
    }

    private String labels(SqlCompletionResponse result) {
        return result.getCandidates().stream()
                .map(candidate -> candidate.getType() + ":" + candidate.getLabel())
                .toList()
                .toString();
    }

    private record CompletionRun(String sql, int cursor, SqlCompletionResponse result) {
    }

    private static final class RecordingMetadataProvider implements ISqlCompletionMetadataProvider {

        private final List<DbSqlCompletionMetadataRequest> requests = new ArrayList<>();

        @Override
        public SqlCompletionMetadataResponse list(DbSqlCompletionMetadataRequest request) {
            requests.add(request);
            return SqlCompletionMetadataResponse.of(List.of());
        }

    }
}
