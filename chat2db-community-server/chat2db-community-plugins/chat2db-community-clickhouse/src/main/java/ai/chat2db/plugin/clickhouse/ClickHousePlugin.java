package ai.chat2db.plugin.clickhouse;


import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class ClickHousePlugin extends ClickHouseSyntaxPlugin implements IPlugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"clickhouse.json", DBConfig.class);
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new ClickHouseMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new ClickHouseDBManager();
    }
}
