package ai.chat2db.plugin.postgresql;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PostgreSQLDBManagerTest {

    @Test
    void buildsDropDatabaseSqlWithStrictIdentifierEscaping() {
        TestPostgreSQLDBManager manage = new TestPostgreSQLDBManager();

        manage.dropDatabase(null, "a\"; DROP DATABASE b; --");

        assertEquals("DROP DATABASE \"a\"\"; DROP DATABASE b; --\"", manage.sql);
    }

    @Test
    void buildsDropSchemaSqlWithoutCascade() {
        TestPostgreSQLDBManager manage = new TestPostgreSQLDBManager();

        manage.dropSchema(null, "app_db", "tenant_schema");

        assertEquals("DROP SCHEMA \"tenant_schema\"", manage.sql);
        assertFalse(manage.sql.contains("CASCADE"));
    }

    private static class TestPostgreSQLDBManager extends PostgreSQLDBManager {
        private String sql;

        @Override
        void executeDropSql(Connection connection, String sql) {
            this.sql = sql;
        }
    }
}
