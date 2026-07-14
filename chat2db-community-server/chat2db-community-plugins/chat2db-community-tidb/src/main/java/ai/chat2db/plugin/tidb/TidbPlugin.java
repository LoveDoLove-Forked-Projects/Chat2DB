package ai.chat2db.plugin.tidb;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class TidbPlugin extends TiDBSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"tidb.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new TidbMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new TidbDBManager();
    }
}
