package ai.chat2db.plugin.bigquery;

import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;


public class BigQueryPlugin extends BigQuerySyntaxPlugin implements IPlugin {

    DBConfig dbConfig;

    @Override
    public DBConfig getDBConfig() {
        if(dbConfig != null){
            return dbConfig;
        }else {
            dbConfig = FileUtils.readJsonValue(this.getClass(), "bigquery.json", DBConfig.class);
            return dbConfig;
        }
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new BigQueryMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new BigQueryDBManager();
    }
}
