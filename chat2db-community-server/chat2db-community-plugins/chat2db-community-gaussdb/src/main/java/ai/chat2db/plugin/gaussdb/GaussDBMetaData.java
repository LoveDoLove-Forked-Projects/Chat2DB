package ai.chat2db.plugin.gaussdb;

import ai.chat2db.plugin.postgresql.PostgreSQLMetaData;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.community.domain.api.model.metadata.TableIndex;
import ai.chat2db.spi.DefaultSQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;

@Slf4j
public class GaussDBMetaData extends PostgreSQLMetaData implements IDbMetaData {
    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "SELECT pg_get_tabledef('" + tableName + "')";
        return DefaultSQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
    }

    @Override
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        return DefaultSQLExecutor.getInstance().indexes(connection, StringUtils.isEmpty(databaseName) ? null : databaseName, StringUtils.isEmpty(schemaName) ? null : schemaName, tableName);
    }


}
