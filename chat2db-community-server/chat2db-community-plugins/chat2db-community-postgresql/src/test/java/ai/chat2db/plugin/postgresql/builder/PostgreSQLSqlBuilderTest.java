package ai.chat2db.plugin.postgresql.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgreSQLSqlBuilderTest {

    @Test
    void shouldUseCtidSubQueryWhenLimitingSingleRowDeleteAndUpdate() {
        PostgreSQLSqlBuilder builder = new PostgreSQLSqlBuilder();
        String where = " where \"a\" = 1 and \"b\" = 2";

        assertEquals("DELETE FROM \"t\" where ctid in (select ctid from \"t\"" + where + " limit 1)",
                builder.appendSingleRowLimit("DELETE", "\"t\"", where, "DELETE FROM \"t\"" + where));
        assertEquals("UPDATE \"t\" set \"a\" = 1 where ctid in (select ctid from \"t\"" + where + " limit 1)",
                builder.appendSingleRowLimit("UPDATE", "\"t\"", where, "UPDATE \"t\" set \"a\" = 1" + where));
    }
}
