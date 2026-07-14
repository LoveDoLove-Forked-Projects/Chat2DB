package ai.chat2db.plugin.hive.constant;

import java.sql.Connection;
import java.sql.SQLException;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.DefaultDBManager;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.springframework.util.StringUtils;


public final class HiveDBManagerConstants {

    public static final String SQL_COPY_TABLE = "CREATE TABLE %s LIKE %s";
    public static final String SQL_DROP_TABLE_EXISTS = "drop table if exists %s";
    public static final String SQL_INSERT_TABLE_SELECT = "INSERT INTO %s SELECT * FROM %s";
    public static final String SQL_USE_DATABASE = "use %s";

    private HiveDBManagerConstants() {
    }
}
