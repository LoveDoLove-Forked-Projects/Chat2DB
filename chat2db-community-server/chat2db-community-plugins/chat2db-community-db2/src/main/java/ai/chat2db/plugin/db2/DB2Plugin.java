package ai.chat2db.plugin.db2;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class DB2Plugin extends DB2SyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"db2.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new DB2MetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new DB2DBManager();
    }
}
