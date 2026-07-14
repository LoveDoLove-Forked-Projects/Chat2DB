package ai.chat2db.plugin.oceanbase.oracle;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class OceanbaseOraclePlugin extends OceanBaseOracleSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"OceanbaseOracle.json", DBConfig.class);

    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new OceanbaseOracleMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new OceanbaseOracleDBManager();
    }
}
