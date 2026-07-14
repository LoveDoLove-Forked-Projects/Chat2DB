package ai.chat2db.plugin.oracle.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OracleSqlBuilderTest {

    @Test
    void shouldUseRowidSubQueryWhenLimitingSingleRowDeleteAndUpdate() {
        OracleSqlBuilder builder = new OracleSqlBuilder();
        String where = " where \"A\" = 1 and \"B\" = 2";

        assertEquals("DELETE FROM \"T\" where rowid in (select rowid from \"T\"" + where + " and rownum = 1)",
                builder.appendSingleRowLimit("DELETE", "\"T\"", where, "DELETE FROM \"T\"" + where));
        assertEquals("UPDATE \"T\" set \"A\" = 1 where rowid in (select rowid from \"T\"" + where + " and rownum = 1)",
                builder.appendSingleRowLimit("UPDATE", "\"T\"", where, "UPDATE \"T\" set \"A\" = 1" + where));
    }
}
