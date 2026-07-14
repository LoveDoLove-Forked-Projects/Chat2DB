package ai.chat2db.plugin.kingbase;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class KingBasePlugin extends KingBaseSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"kingbase.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new KingBaseMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new KingBaseDBManager();
    }
}
