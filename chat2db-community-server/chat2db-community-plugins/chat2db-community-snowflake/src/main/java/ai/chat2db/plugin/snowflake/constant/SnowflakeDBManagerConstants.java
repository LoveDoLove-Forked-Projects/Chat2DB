package ai.chat2db.plugin.snowflake.constant;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.DefaultDBManager;
import ai.chat2db.community.domain.api.model.datasource.KeyValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public final class SnowflakeDBManagerConstants {

    public static final String SQL_DROP_TABLE = "DROP TABLE %s";
    public static final String SQL_USE_DATABASE = "USE DATABASE \"%s\";";
    public static final String SQL_USE_SCHEMA = "USE SCHEMA \"%s\".%s;";
    public static final String IDENTIFIER_QUOTE = "\"";

    private SnowflakeDBManagerConstants() {
    }
}
