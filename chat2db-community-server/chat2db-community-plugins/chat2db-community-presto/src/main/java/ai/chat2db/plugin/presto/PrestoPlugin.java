package ai.chat2db.plugin.presto;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class PrestoPlugin extends PrestoSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"presto.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new PrestoMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new PrestoDBManager();
    }
}
