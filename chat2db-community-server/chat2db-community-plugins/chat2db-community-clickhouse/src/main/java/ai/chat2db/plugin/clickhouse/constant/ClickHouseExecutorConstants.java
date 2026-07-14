package ai.chat2db.plugin.clickhouse.constant;

import ai.chat2db.spi.DefaultSQLExecutor;

import java.sql.Connection;
import java.sql.SQLException;


public final class ClickHouseExecutorConstants {

    public static final String SQL_EXPLAIN_AST = "EXPLAIN AST %s";
    public static final String EXPLAIN_COLUMN = "explain";
    public static final String SELECT_WITH_UNION_QUERY = "selectwithunionquery";
    public static final String SELECT_QUERY = "selectquery";

    private ClickHouseExecutorConstants() {
    }
}
