package ai.chat2db.community.start.test.jsqlparser;

import ai.chat2db.spi.lineage.JSqlParserLineageFinder;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSqlParserLineageTest {

    private static JSqlParserLineageFinder.DatabaseConfig databaseConfig;

    @BeforeAll
    public static void setUp() {
        databaseConfig = JSqlParserLineageFinder.DatabaseConfig.builder()
                .databaseType("mysql")
                .supportDatabase(true)
                .supportSchema(false)
                .build();
    }

    @Test
    public void testSelectSQL() {
        assertSql("SELECT * FROM test_db.users",
                "SELECT", "test_db", null, "users");
        assertSql("SELECT u.name FROM test_db.users u",
                "SELECT", "test_db", null, "users");
        assertSql("SELECT u.name, o.order_id FROM test_db.users u JOIN test_db.orders o ON u.id = o.user_id",
                Arrays.asList(
                        new TableInfo("test_db", null, "users"),
                        new TableInfo("test_db", null, "orders")
                ));
    }

    @Test
    public void testInsertSQL() {
        assertSql("INSERT INTO test_db.users (name, age) VALUES ('Tom', 20)",
                "INSERT", "test_db", null, "users");

    }

    @Test
    public void testUpdateSQL() {
        assertSql("UPDATE test_db.users SET name = 'Tom' WHERE id = 1",
                "UPDATE", "test_db", null, "users");

    }

    @Test
    public void testDeleteSQL() {
        assertSql("DELETE FROM test_db.users WHERE id = 1",
                "DELETE", "test_db", null, "users");
    }
    private void assertSql(String sql, String expectedType, String expectedDb, String expectedSchema, String expectedTable) {
        SimpleSqlStatement result = JSqlParserLineageFinder.findLineage(sql, databaseConfig);
        assertEquals(expectedType, result.getSqlType());

        List<SimpleSqlStatement.SimpleTable> tables = result.getTables();
        assertNotNull(tables);
        assertEquals(1, tables.size());

        SimpleSqlStatement.SimpleTable table = tables.get(0);
        assertEquals(expectedDb, table.getDatabaseName());
        assertEquals(expectedSchema, table.getSchemaName());
        assertEquals(expectedTable, table.getTableName());
    }
    private void assertSql(String sql, List<TableInfo> expectedTables) {
        SimpleSqlStatement result = JSqlParserLineageFinder.findLineage(sql, databaseConfig);
        List<SimpleSqlStatement.SimpleTable> tables = result.getTables();

        assertNotNull(tables);
        assertEquals(expectedTables.size(), tables.size());

        for (int i = 0; i < expectedTables.size(); i++) {
            TableInfo expected = expectedTables.get(i);
            SimpleSqlStatement.SimpleTable actual = tables.get(i);

            assertEquals(expected.database, actual.getDatabaseName());
            assertEquals(expected.schema, actual.getSchemaName());
            assertEquals(expected.table, actual.getTableName());
        }
    }
    @AllArgsConstructor
    private static class TableInfo {
        String database;
        String schema;
        String table;
    }
}
