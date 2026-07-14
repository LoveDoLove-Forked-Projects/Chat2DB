package ai.chat2db.community.domain.api.model.console;

import ai.chat2db.community.domain.api.model.cache.CacheTree;
import java.io.Serializable;
import lombok.Data;


@Data
public class ConsoleParserCache implements Serializable {
    private static final long serialVersionUID = 1L;

    private CacheTree cacheTree;


    public ConsoleParserCache() {
        this.cacheTree = new CacheTree();
    }


    public void addDataSource(String datasourceName, String datasourceId) {
        cacheTree.addDataSource(datasourceName, datasourceId);
    }

    public boolean hasTable(String datasourceId, String dbName,
                            String schemaName, String tableName,
                            String identifierAlias, boolean hasDatabase, boolean hasSchema) {
        return cacheTree.tableExists(datasourceId, dbName, schemaName,
                                     tableName, identifierAlias, hasDatabase, hasSchema);
    }

    public void addTable(String dataSourceId, String dbName,
                         String schemaName, String tableName, String alias,
                         boolean hasDatabase, boolean hasSchema) {
        cacheTree.addTable(dataSourceId, dbName, schemaName,
                           tableName, alias,
                           hasDatabase, hasSchema);
    }

}
