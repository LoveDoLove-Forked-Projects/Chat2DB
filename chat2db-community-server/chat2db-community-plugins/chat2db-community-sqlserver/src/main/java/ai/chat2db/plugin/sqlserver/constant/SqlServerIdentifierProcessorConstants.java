package ai.chat2db.plugin.sqlserver.constant;

import ai.chat2db.spi.DefaultSQLIdentifierProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public final class SqlServerIdentifierProcessorConstants {

    public static final Pattern SQL_SERVER_PATTERN = Pattern.compile("[\\[\"'](.*?)[]\"]");

    private SqlServerIdentifierProcessorConstants() {
    }
}
