package ai.chat2db.plugin.oscar;

import ai.chat2db.plugin.oscar.constant.OscarConstants;
import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class OscarPlugin implements IPlugin {

    private DBConfig dbConfig;

    @Override
    public DBConfig getDBConfig() {
        if (dbConfig != null) {
            return dbConfig;
        }
        dbConfig = FileUtils.readJsonValue(this.getClass(), OscarConstants.CONFIG_FILE, DBConfig.class);
        return dbConfig;
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new OscarMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new OscarDBManager();
    }
}
