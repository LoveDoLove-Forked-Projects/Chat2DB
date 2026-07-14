package ai.chat2db.plugin.oracle;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class OraclePlugin extends OracleSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"oracle.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new OracleMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new OracleDBManager();
    }
}
