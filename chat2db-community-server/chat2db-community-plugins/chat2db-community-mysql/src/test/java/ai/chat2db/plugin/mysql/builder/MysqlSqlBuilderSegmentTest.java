package ai.chat2db.plugin.mysql.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MysqlSqlBuilderSegmentTest {

    @Test
    void quotesIdentifiersThroughUnifiedSegment() {
        MysqlSqlBuilder builder = new MysqlSqlBuilder();

        assertEquals("`users`", builder.identifier().quoteIdentifier("users"));
        assertEquals("`app`.`users`", builder.identifier().quoteQualifiedIdentifier("app", null, "users"));
        assertEquals("USE `app`", builder.ddl().database().buildUseDatabase("app"));
        assertEquals("SELECT COUNT(1) FROM `app`.`users`", builder.dql().buildSelectCount("app", null, "users"));
    }
}
