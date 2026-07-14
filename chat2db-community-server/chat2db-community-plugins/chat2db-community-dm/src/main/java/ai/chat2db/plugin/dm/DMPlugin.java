package ai.chat2db.plugin.dm;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class DMPlugin extends DMSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"dm.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new DMMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new DMDBManager();
    }
}
