package ai.chat2db.community.test.spi.util;

import ai.chat2db.spi.util.JdbcUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcUtilsTest {

    @Test
    void replaceUrlHostAndPortForSshRewritesBracketedIpv6Host() {
        String url = JdbcUtils.replaceUrlHostAndPortForSsh(
                "jdbc:mysql://[2001:db8::1002]:13306/demo",
                "2001:db8::1002",
                "13306",
                "49152"
        );

        assertEquals("jdbc:mysql://127.0.0.1:49152/demo", url);
    }

    @Test
    void replaceUrlHostAndPortForSshRewritesAlreadyBracketedHostValue() {
        String url = JdbcUtils.replaceUrlHostAndPortForSsh(
                "jdbc:mysql://[2001:db8::1002]:13306/",
                "[2001:db8::1002]",
                "13306",
                "49152"
        );

        assertEquals("jdbc:mysql://127.0.0.1:49152/", url);
    }

    @Test
    void replaceUrlHostAndPortForSshKeepsIpv4AndHostnameBehavior() {
        String url = JdbcUtils.replaceUrlHostAndPortForSsh(
                "jdbc:mysql://mysql.example.com:3306/demo",
                "mysql.example.com",
                "3306",
                "49152"
        );

        assertEquals("jdbc:mysql://127.0.0.1:49152/demo", url);
    }
}
