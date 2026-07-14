package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.cache.CacheNode;
import lombok.Data;


@Data
public class DatabaseNode extends CacheNode {


    private String datasourceName;

    public DatabaseNode(String name) {
        super(name);
    }


    public void addSchema(String schemaName) {
        if (!this.hasChild(schemaName)) {
            SchemaNode schemaNode = new SchemaNode(schemaName);
            schemaNode.setDatabaseName(this.getName());
            this.addChild(schemaNode);
        }

    }

}
