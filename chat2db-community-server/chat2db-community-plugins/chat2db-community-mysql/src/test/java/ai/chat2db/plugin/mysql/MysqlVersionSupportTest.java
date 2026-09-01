package ai.chat2db.plugin.mysql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MysqlVersionSupportTest {

    @Test
    void shouldGateInvisibleColumnsAtMysql8023() {
        assertFalse(MysqlVersionSupport.supportsInvisibleColumns("5.7.44-log"));
        assertFalse(MysqlVersionSupport.supportsInvisibleColumns("8.0.22"));
        assertTrue(MysqlVersionSupport.supportsInvisibleColumns("8.0.23"));
        assertTrue(MysqlVersionSupport.supportsInvisibleColumns("8.4.0-commercial"));
        assertFalse(MysqlVersionSupport.supportsInvisibleColumns("10.6.0-MariaDB"));
        assertFalse(MysqlVersionSupport.supportsInvisibleColumns(null));
    }
}
