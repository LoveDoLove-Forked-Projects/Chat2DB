package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.mysql.parser.base.MySqlParser;

import java.util.regex.Pattern;


public final class MysqlSqlParserConstants {

    public static final Pattern DELIMITER_PATTERN = Pattern.compile("DELIMITER\\s+([^a-zA-Z0-9\\-#;,+)]+)");

    private MysqlSqlParserConstants() {
    }
}
