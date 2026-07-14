import ai.chat2db.plugin.cockroachdb.parser.CockroachSqlParser;
import ai.chat2db.plugin.mysql.parser.MysqlSqlParser;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.mysql.parser.base.MySqlParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompletionSyntaxCoverageTest {

    @Test
    void parsesCurrentMysqlReuseNonCrudCompletionSyntax() {
        assertParses("""
                START TRANSACTION;
                SAVEPOINT sp1;
                RELEASE SAVEPOINT sp1;
                ROLLBACK TO SAVEPOINT sp1;
                COMMIT;
                GRANT SELECT, INSERT ON app.orders TO analyst;
                REVOKE INSERT ON app.orders FROM analyst;
                EXPLAIN SELECT * FROM orders WHERE id = 1;
                DESCRIBE orders;
                SHOW TABLES;
                CALL refresh_orders(1, 'daily');
                WITH recent AS (
                    SELECT id, amount FROM orders
                    WHERE created_at > current_timestamp - interval 7 day
                )
                SELECT id FROM recent;
                SELECT user_id,
                       row_number() OVER (PARTITION BY account_id ORDER BY created_at DESC) AS rn,
                       sum(amount) OVER w AS total
                FROM orders
                WINDOW w AS (
                    PARTITION BY account_id
                    ORDER BY created_at
                    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                );
                INSERT INTO orders (id, amount)
                VALUES (1, 10)
                ON DUPLICATE KEY UPDATE amount = VALUES(amount);
                """);
    }

    @Test
    void documentsCurrentMysqlParserReuseBoundary() {
        Assertions.assertTrue(MysqlSqlParser.class.isAssignableFrom(CockroachSqlParser.class));
    }

    private static void assertParses(String sql) {
        MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokenStream);
        CountingErrorListener listener = new CountingErrorListener();
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);

        parser.root();

        Assertions.assertEquals(0, listener.errorCount);
    }

    private static class CountingErrorListener extends BaseErrorListener {
        private int errorCount;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
            errorCount++;
        }
    }
}
