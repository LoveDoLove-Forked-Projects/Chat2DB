import ai.chat2db.plugin.hive.parser.HiveSqlParser;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HiveCompletionSyntaxCoverageTest {

    private final HiveSqlParser parser = new HiveSqlParser();

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
    void documentsHiveNativeMergeSyntaxOutsideMysqlReuseBoundary() {
        assertDoesNotParse("""
                MERGE INTO target_table t
                USING source_table s
                ON t.id = s.id
                WHEN MATCHED THEN UPDATE SET name = s.name;
                """);
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
