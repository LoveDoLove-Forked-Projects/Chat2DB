package ai.chat2db.community.domain.api.model.datasource;

import ai.chat2db.community.domain.api.model.cache.CacheNode;
import ai.chat2db.community.domain.api.model.db.DatabaseNode;
import lombok.Data;


@Data
public class DataSourceNode extends CacheNode {

    private String datasourceName;

    public DataSourceNode(String name) {
        super(name);
    }

    public void addDatabase(String dbName) {
        if (!this.hasChild(dbName)) {
            DatabaseNode databaseNode = new DatabaseNode(dbName);
            databaseNode.setDatasourceName(datasourceName);
            this.addChild(databaseNode);
        }
    }
}
