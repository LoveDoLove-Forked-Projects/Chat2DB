package ai.chat2db.plugin.clickhouse;

import ai.chat2db.spi.DefaultSQLExecutor;

import java.sql.Connection;
import java.sql.SQLException;

import static ai.chat2db.plugin.clickhouse.constant.ClickHouseExecutorConstants.*;
public class ClickHouseExecutor extends DefaultSQLExecutor {





    @Override
    public boolean isQueryCommand(Connection connection, String sql) {
        boolean query = DefaultSQLExecutor.getInstance().execute(connection, String.format(SQL_EXPLAIN_AST, sql), resultSet -> {
            try {
                resultSet.next();
                String explain = resultSet.getString(EXPLAIN_COLUMN);
                if (explain != null && (explain.toLowerCase().startsWith(SELECT_WITH_UNION_QUERY)
                        ||explain.toLowerCase().startsWith(SELECT_QUERY)
                )) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                return false;
            }
        });
        return query;
    }
}
