package ai.chat2db.plugin.hive;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class HivePlugin extends HiveSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"hive.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new HiveMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new HiveDBManager();
    }
}
