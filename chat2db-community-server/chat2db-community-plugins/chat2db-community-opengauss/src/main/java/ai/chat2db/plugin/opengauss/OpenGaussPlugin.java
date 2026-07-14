package ai.chat2db.plugin.opengauss;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class OpenGaussPlugin extends OpenGaussSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"opengauss.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new OpenGaussMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new OpenGaussDBManager();
    }
}
