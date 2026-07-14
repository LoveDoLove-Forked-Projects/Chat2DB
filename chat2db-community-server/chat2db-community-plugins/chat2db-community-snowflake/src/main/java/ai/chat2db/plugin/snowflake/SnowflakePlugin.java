package ai.chat2db.plugin.snowflake;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;

import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class SnowflakePlugin extends SnowFlakeSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"snowflake.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new SnowflakeMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new SnowflakeDBManager();
    }
}
