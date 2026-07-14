package ai.chat2db.plugin.sundb;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class SUNDBPlugin extends SUNDBSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"sundb.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new SUNDBMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new SUNDBDBManager();
    }
}
