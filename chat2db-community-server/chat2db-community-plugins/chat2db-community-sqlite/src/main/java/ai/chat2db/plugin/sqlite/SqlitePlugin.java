package ai.chat2db.plugin.sqlite;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class SqlitePlugin extends SqliteSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"sqlite.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new SqliteMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new SqliteDBManager();
    }
}
