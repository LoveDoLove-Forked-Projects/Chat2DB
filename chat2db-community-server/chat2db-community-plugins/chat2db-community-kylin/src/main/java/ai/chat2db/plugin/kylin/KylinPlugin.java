package ai.chat2db.plugin.kylin;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class KylinPlugin extends KylinSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"kylin.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new KylinMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new KylinDBManager();
    }
}
