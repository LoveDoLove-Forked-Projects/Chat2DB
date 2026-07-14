package ai.chat2db.plugin.kingbase;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.DefaultDBManager;
import ai.chat2db.community.domain.api.model.async.AsyncContext;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.DefaultSQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

import static ai.chat2db.plugin.kingbase.constant.KingBaseDBManagerConstants.*;
@Slf4j
public class KingBaseDBManager extends DefaultDBManager implements IDbManager {



    @Override
    public Connection getConnection(ConnectInfo connectInfo) {
        String url = connectInfo.getUrl();
        String database = connectInfo.getDatabaseName();
        if (database != null && !database.isEmpty()) {
            url = replaceDatabaseInJdbcUrl(url, database);
        }
        connectInfo.setUrl(url);
        String schemaName = connectInfo.getSchemaName();
        connectInfo.setSchemaName(null);
        Connection connection = super.getConnection(connectInfo);
        if (StringUtils.isNotBlank(schemaName)) {
            String sql = String.format(SQL_SET_SEARCH_PATH_USER_PUBLIC, schemaName);
            try {
                DefaultSQLExecutor.getInstance().execute(connection, sql);
            } catch (SQLException e) {
                log.error("connectDatabase error", e);
            }
            connectInfo.setSchemaName(schemaName);
        }
        return connection;

    }


    public String replaceDatabaseInJdbcUrl(String url, String newDatabase) {
        String[] urlAndParams = url.split("\\?");
        String urlWithoutParams = urlAndParams[0];
        String[] parts = urlWithoutParams.split("/");
        parts[parts.length - 1] = newDatabase;
        String newUrlWithoutParams = String.join("/", parts);
        String newUrl = urlAndParams.length > 1 ? newUrlWithoutParams + "?" + urlAndParams[1] : newUrlWithoutParams;

        return newUrl;
    }


    @Override
    public String dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "drop table if exists " +tableName;
        return sql;
    }

    @Override
    public void copyTable(Connection connection, String databaseName, String schemaName, String tableName, String newTableName,boolean copyData) throws SQLException {
        String sql = "";
        if(copyData){
            sql = "CREATE TABLE " + newTableName + " AS TABLE " + tableName + " WITH DATA";
        }else {
            sql = "CREATE TABLE " + newTableName + " AS TABLE " + tableName + " WITH NO DATA";
        }
        DefaultSQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }

    @Override
    public void exportTableData(Connection connection, String databaseName, String schemaName, String tableName, AsyncContext asyncContext) {
        exportTableData(connection, databaseName, schemaName, tableName, asyncContext, 10000);
    }
}
