package ai.chat2db.plugin.sqlserver.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlServerSqlBuilderTest {

    @Test
    void shouldKeepGoDelimiterForShowplanXmlBatch() {
        SqlServerSqlBuilder builder = new SqlServerSqlBuilder();

        String sql = "SELECT * FROM uf_wtbhb WHERE lcid=1208045;";

        assertEquals("SET SHOWPLAN_XML ON;\nGO\n"
                + "SELECT * FROM uf_wtbhb WHERE lcid=1208045;\n"
                + "GO\nSET SHOWPLAN_XML OFF;", builder.dql().buildExplain(sql));
    }

    @Test
    void shouldUseTopWhenLimitingSingleRowDeleteAndUpdate() {
        SqlServerSqlBuilder builder = new SqlServerSqlBuilder();
        String where = " where [a] = 1 and [b] = 2";

        assertEquals("DELETE TOP (1) FROM [t]" + where,
                builder.appendSingleRowLimit("DELETE", "[t]", where, "DELETE FROM [t]" + where));
        assertEquals("UPDATE TOP (1) [t] set [a] = 1" + where,
                builder.appendSingleRowLimit("UPDATE", "[t]", where, "UPDATE [t] set [a] = 1" + where));
    }
}
