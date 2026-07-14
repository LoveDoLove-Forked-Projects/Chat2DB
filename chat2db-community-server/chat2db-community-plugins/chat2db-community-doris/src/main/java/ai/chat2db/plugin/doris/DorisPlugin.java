package ai.chat2db.plugin.doris;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class DorisPlugin extends DorisSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"doris.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new DorisMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new DorisDBManager();
    }
}
