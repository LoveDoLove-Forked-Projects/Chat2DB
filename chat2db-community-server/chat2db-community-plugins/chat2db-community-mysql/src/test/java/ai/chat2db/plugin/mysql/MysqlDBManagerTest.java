package ai.chat2db.plugin.mysql;

import ai.chat2db.community.tools.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MysqlDBManagerTest {

    @Test
    void buildsDropDatabaseSqlWithStrictIdentifierEscaping() {
        TestMysqlDBManager manage = new TestMysqlDBManager();

        manage.dropDatabase(null, "a`; DROP DATABASE b; --");

        assertEquals("DROP DATABASE `a``; DROP DATABASE b; --`", manage.sql);
    }

    @Test
    void rejectsSchemaDropInsteadOfMappingItToDatabaseDrop() {
        MysqlDBManager manage = new MysqlDBManager();

        assertThrows(BusinessException.class, () -> manage.dropSchema(null, "app_db", "app_schema"));
    }

    private static class TestMysqlDBManager extends MysqlDBManager {
        private String sql;

        @Override
        void executeDropSql(Connection connection, String sql) {
            this.sql = sql;
        }
    }
}
