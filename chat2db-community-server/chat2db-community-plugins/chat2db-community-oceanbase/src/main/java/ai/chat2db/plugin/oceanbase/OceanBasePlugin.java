package ai.chat2db.plugin.oceanbase;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class OceanBasePlugin extends OceanBaseSyntaxPlugin implements IPlugin {

    private DBConfig dbConfig;
    @Override
    public DBConfig getDBConfig() {
        if(dbConfig != null){
            return dbConfig;
        }
        dbConfig = FileUtils.readJsonValue(this.getClass(),"oceanbase.json", DBConfig.class);
        return dbConfig;
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new OceanBaseMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new OceanBaseDBManager();
    }
}
