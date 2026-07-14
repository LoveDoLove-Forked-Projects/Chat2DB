import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import ai.chat2db.plugin.starrocks.parser.StarrocksSqlParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StarrocksCompletionSyntaxCoverageTest {

    private final StarrocksSqlParser parser = new StarrocksSqlParser();

    @Test
    void coversNonCrudSyntaxThroughMysqlReuse() {
        assertParses("""
                COMMIT;
                ROLLBACK;
                SAVEPOINT before_load;
                GRANT SELECT ON analytics.events TO 'analyst'@'%';
                REVOKE SELECT ON analytics.events FROM 'analyst'@'%';
                EXPLAIN SELECT * FROM analytics.events;
                SHOW TABLES;
                DESCRIBE analytics.events;
                CALL refresh_stats();
                WITH daily AS (SELECT user_id, COUNT(*) AS cnt FROM events GROUP BY user_id)
                SELECT user_id, cnt, ROW_NUMBER() OVER (ORDER BY cnt DESC) rn FROM daily;
                """);
    }

    @Test
    void documentsMysqlReuseBoundaryForNativeMerge() {
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
