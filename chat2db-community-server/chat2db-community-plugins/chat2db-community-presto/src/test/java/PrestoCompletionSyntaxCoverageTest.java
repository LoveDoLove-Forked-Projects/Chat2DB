import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.plugin.presto.parser.PrestoSqlParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrestoCompletionSyntaxCoverageTest {

    private final PrestoSqlParser parser = new PrestoSqlParser();

    @Test
    void coversReusableNonCrudSyntaxThroughMysqlParser() {
        assertParses("""
                COMMIT;
                ROLLBACK;
                EXPLAIN SELECT * FROM events;
                SHOW TABLES;
                DESCRIBE events;
                WITH daily AS (SELECT user_id, COUNT(*) AS cnt FROM events GROUP BY user_id)
                SELECT user_id, cnt, ROW_NUMBER() OVER (ORDER BY cnt DESC) rn FROM daily;
                """);
    }

    @Test
    void documentsPrestoNativeCallSyntaxOutsideMysqlReuseBoundary() {
        assertDoesNotParse("CALL system.runtime.kill_query('query_id', 'reason');");
    }

    private void assertParses(String sql) {
        SqlParserResponse result = parser.parserStatements(sql);
        Assertions.assertTrue(result.getSyntaxErrors().isEmpty(), result.getSyntaxErrors().toString());
    }

    private void assertDoesNotParse(String sql) {
        SqlParserResponse result = parser.parserStatements(sql);
        Assertions.assertFalse(result.getSyntaxErrors().isEmpty());
    }
}
