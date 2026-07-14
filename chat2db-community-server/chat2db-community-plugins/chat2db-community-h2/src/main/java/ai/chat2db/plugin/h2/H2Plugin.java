package ai.chat2db.plugin.h2;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.ISqlSyntaxPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.DefaultMetaService;
import ai.chat2db.spi.util.FileUtils;

public class H2Plugin extends DefaultMetaService implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"h2.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new H2Meta();
    }

    @Override
    public IDbManager getDbManager() {
        return new H2DBManager();
    }

    @Override
    public ISqlSyntaxPlugin getSqlSyntaxPlugin() {
        return new H2SyntaxPlugin();
    }
}
