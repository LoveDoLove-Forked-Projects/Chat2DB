import ai.chat2db.plugin.postgresql.parser.PgsqlSqlParser;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLLexer;
import ai.chat2db.plugin.postgresql.parser.base.PostgreSQLParser;
import ai.chat2db.plugin.redshift.parser.RedShiftSqlParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompletionSyntaxCoverageTest {

    @Test
    void parsesPostgreSqlReuseNonCrudCompletionSyntax() {
        assertParses(PostgreSqlReuseCoverage.SQL);
    }

    @Test
    void reusesPostgreSqlParserBoundary() {
        Assertions.assertTrue(PgsqlSqlParser.class.isAssignableFrom(RedShiftSqlParser.class));
    }

    private static void assertParses(String sql) {
        PostgreSQLLexer lexer = new PostgreSQLLexer(CharStreams.fromString(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(tokenStream);
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

    private static class PostgreSqlReuseCoverage {
        private static final String SQL = """
                BEGIN;
                SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
                SAVEPOINT sp1;
                RELEASE SAVEPOINT sp1;
                ROLLBACK TO SAVEPOINT sp1;
                COMMIT;
                GRANT SELECT, INSERT ON TABLE public.orders TO analyst;
                REVOKE INSERT ON TABLE public.orders FROM analyst;
                EXPLAIN (ANALYZE, VERBOSE, COSTS TRUE) SELECT * FROM public.orders WHERE id = 1;
                SHOW search_path;
                CALL public.refresh_orders(1, 'daily');
                WITH recent AS (
                    SELECT id, amount FROM public.orders
                    WHERE created_at > now() - interval '7 days'
                )
                SELECT id FROM recent;
                MERGE INTO public.orders AS t
                USING public.order_updates AS s
                ON t.id = s.id
                WHEN MATCHED THEN UPDATE SET amount = s.amount
                WHEN NOT MATCHED THEN INSERT (id, amount) VALUES (s.id, s.amount);
                SELECT user_id,
                       row_number() OVER (PARTITION BY account_id ORDER BY created_at DESC) AS rn,
                       sum(amount) OVER w AS total
                FROM public.orders
                WINDOW w AS (
                    PARTITION BY account_id
                    ORDER BY created_at
                    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                );
                CREATE SUBSCRIPTION sub_orders
                CONNECTION 'host=localhost dbname=orders'
                PUBLICATION pub_orders
                WITH (copy_data = false);
                ALTER SYSTEM SET work_mem = '64MB';
                CREATE POLICY order_policy ON public.orders
                FOR SELECT TO analyst
                USING (account_id = current_setting('app.account_id')::int);
                """;
    }
}
