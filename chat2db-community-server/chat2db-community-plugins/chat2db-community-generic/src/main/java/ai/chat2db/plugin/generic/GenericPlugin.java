package ai.chat2db.plugin.generic;


import ai.chat2db.community.domain.api.enums.parser.DatabaseTypeEnum;
import ai.chat2db.plugin.duckdb.DuckDBSyntaxPlugin;
import ai.chat2db.plugin.elasticsearch.ElasticSearchSyntaxPlugin;
import ai.chat2db.plugin.tdengine.TDengineSyntaxPlugin;
import ai.chat2db.spi.IDbManager;
import ai.chat2db.spi.IDbMetaData;
import ai.chat2db.spi.IPlugin;
import ai.chat2db.spi.ISqlSyntaxPlugin;
import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GenericPlugin implements IPlugin {

    private DBConfig dbConfig;

    @Override
    public DBConfig getDBConfig() {
        return this.dbConfig;
    }

    @Override
    public IDbMetaData getDbMetaData() {
        return new GenericMetaData();
    }

    @Override
    public IDbManager getDbManager() {
        return new GenericDBManager();
    }

    @Override
    public ISqlSyntaxPlugin getSqlSyntaxPlugin() {
        if (dbConfig == null || StringUtils.isBlank(dbConfig.getDbType())) {
            return null;
        }
        DatabaseTypeEnum databaseType = DatabaseTypeEnum.from(dbConfig.getDbType());
        if (databaseType == DatabaseTypeEnum.DUCKDB) {
            return new DuckDBSyntaxPlugin();
        }
        if (databaseType == DatabaseTypeEnum.ELASTICSEARCH) {
            return new ElasticSearchSyntaxPlugin();
        }
        if (databaseType == DatabaseTypeEnum.TDENGINE) {
            return new TDengineSyntaxPlugin();
        }
        return null;
    }

    @Override
    public List<DBConfig> getDBConfigList() {
        return FileUtils.readJsonValueAsList(this.getClass(), "generic.json", DBConfig.class);
    }

    @Override
    public IPlugin getPlugin(DBConfig dbConfig) {
        GenericPlugin plugin = new GenericPlugin();
        plugin.dbConfig = dbConfig;
        return plugin;
    }
}
