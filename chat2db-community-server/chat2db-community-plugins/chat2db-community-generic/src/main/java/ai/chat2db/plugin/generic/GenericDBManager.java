package ai.chat2db.plugin.generic;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.DefaultDBManager;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.DefaultSQLExecutor;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class GenericDBManager extends DefaultDBManager implements IDbManager {

    @Override
    public void connectDatabase(Connection connection, String database) {
        if (StringUtils.isEmpty(database)) {
            return;
        }
        String schema = Chat2DBContext.getConnectInfo().getSchemaName();
        String changeDatabase = Chat2DBContext.getDBConfig().getChangeDatabase(database, schema);
        if(StringUtils.isEmpty(changeDatabase)){
            return;
        }
        try {
            DefaultSQLExecutor.getInstance().execute(connection, changeDatabase);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
