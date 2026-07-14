package ai.chat2db.plugin.h2;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.DefaultDBManager;
import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import static ai.chat2db.plugin.h2.constant.H2DBManagerConstants.*;
public class H2DBManager extends DefaultDBManager implements IDbManager {



    @Override
    public void exportDatabase(Connection connection, String databaseName, String schemaName, AsyncContext asyncContext) throws SQLException {
        exportSchema(connection, schemaName, asyncContext);
    }

    private void exportSchema(Connection connection, String schemaName, AsyncContext asyncContext) throws SQLException {
        String sql = String.format("SCRIPT NODATA NOPASSWORDS NOSETTINGS DROP SCHEMA %s;", schemaName);
        if (asyncContext.isContainsData()) {
            sql = sql.replace("NODATA", "");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String script = resultSet.getString("SCRIPT");
                if (!(script.startsWith("CREATE USER")||script.startsWith("--"))) {
                    StringBuilder sqlBuilder = new StringBuilder();
                    sqlBuilder.append(script);
                    sqlBuilder.append("\n");
                    asyncContext.write(sqlBuilder.toString());
                }
            }
        }

    }

    @Override
    public void connectDatabase(Connection connection, String database) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (ObjectUtils.anyNull(connectInfo) || StringUtils.isEmpty(connectInfo.getSchemaName())) {
            return;
        }
        String schemaName = connectInfo.getSchemaName();
        try {
            DefaultSQLExecutor.getInstance().execute(connection, String.format(SQL_SET_SCHEMA, schemaName));
        } catch (SQLException e) {

        }
    }


    @Override
    public String dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        return String.format(SQL_DROP_TABLE, tableName);
    }
}
