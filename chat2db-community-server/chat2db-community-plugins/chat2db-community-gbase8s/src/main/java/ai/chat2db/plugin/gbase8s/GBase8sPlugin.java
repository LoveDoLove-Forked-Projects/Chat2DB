package ai.chat2db.plugin.gbase8s;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class GBase8sPlugin implements IPlugin {

    private DBConfig dbConfig;

    @Override
    public DBConfig getDBConfig() {
        if(dbConfig != null){
            return dbConfig;
        }
        dbConfig = FileUtils.readJsonValue(this.getClass(),"gbase8s.json", DBConfig.class);
        return dbConfig;
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new GBase8sMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new GBase8sDBManager();
    }
}
