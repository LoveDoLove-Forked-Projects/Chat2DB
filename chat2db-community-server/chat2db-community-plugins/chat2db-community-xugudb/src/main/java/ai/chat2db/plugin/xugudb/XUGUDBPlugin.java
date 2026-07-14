package ai.chat2db.plugin.xugudb;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class XUGUDBPlugin extends XUGUDBSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"xugudb.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new XUGUDBMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new XUGUDBManager();
    }
}
