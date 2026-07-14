package ai.chat2db.plugin.sqlserver;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class SqlServerPlugin extends SqlServerSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"sqlserver.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new SqlServerMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new SqlServerDBManager();
    }
}
