import ai.chat2db.spi.DefaultSqlSyntaxHandler;
import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.community.domain.api.model.parser.message.SyntaxErrorMessage;
import ai.chat2db.community.domain.api.model.parser.result.SqlParserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TiDBCompletionSyntaxCoverageTest {

    @Test
    public void testMysqlFamilyNonCrudSyntaxThroughTiDBPlugin() {
        String sql = """
                START TRANSACTION READ ONLY;
                SAVEPOINT sp_tidb;
                ROLLBACK TO SAVEPOINT sp_tidb;
                GRANT SELECT ON app_db.* TO 'reporter'@'%';
                SHOW GRANTS FOR 'reporter'@'%';
                EXPLAIN SELECT id FROM user_info;
                DESCRIBE user_info;
                SHOW FULL TABLES FROM app_db LIKE 'user%';
                CALL refresh_user_summary(1);
                WITH recent_orders AS (SELECT user_id FROM orders) SELECT user_id FROM recent_orders;
                REPLACE INTO user_info (id, name) VALUES (1, 'Tom');
                INSERT INTO user_info (id, name) VALUES (1, 'Tom') ON DUPLICATE KEY UPDATE name = VALUES(name);
                SELECT user_id, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at) AS rn FROM orders;
                FLUSH TABLES WITH READ LOCK;
                COMMIT;
                """;

        assertParses(sql, DatabaseTypeEnum.TIDB);
    }

    private void assertParses(String sql, DatabaseTypeEnum databaseType) {
        SqlParserResponse result = DefaultSqlSyntaxHandler.parserStatements(sql, databaseType);
        List<SyntaxErrorMessage> syntaxErrors = result.getSyntaxErrors();
        Assertions.assertTrue(syntaxErrors == null || syntaxErrors.isEmpty(), () -> syntaxErrors.toString());
        Assertions.assertNotNull(result.getStatements());
    }
}
