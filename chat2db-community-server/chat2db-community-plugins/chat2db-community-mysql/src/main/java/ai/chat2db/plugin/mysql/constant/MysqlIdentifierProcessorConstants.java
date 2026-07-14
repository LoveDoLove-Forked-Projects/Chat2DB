package ai.chat2db.plugin.mysql.constant;

import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;



public final class MysqlIdentifierProcessorConstants {

    public static final Pattern MYSQL_PATTERN = Pattern.compile("[`\"](.*?)[`\"]");

    private MysqlIdentifierProcessorConstants() {
    }
}
