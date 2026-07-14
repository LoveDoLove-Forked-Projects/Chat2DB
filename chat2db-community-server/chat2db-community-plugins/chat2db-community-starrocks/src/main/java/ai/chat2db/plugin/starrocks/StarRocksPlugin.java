package ai.chat2db.plugin.starrocks;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class StarRocksPlugin extends StarrocksSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"starrocks.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new StarRocksMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new StarRocksDBManager();
    }
}
