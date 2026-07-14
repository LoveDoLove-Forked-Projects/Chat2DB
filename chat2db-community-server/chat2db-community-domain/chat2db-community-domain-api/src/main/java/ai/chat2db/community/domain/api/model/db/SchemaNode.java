package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.cache.CacheNode;
import lombok.Data;


@Data
public class SchemaNode extends CacheNode {


    private String databaseName;

    public SchemaNode(String name) {
        super(name);
    }


    public String addTable(String tableName, String alias) {
        if (!this.hasChild(tableName)) {
            TableNode tableNode = new TableNode(tableName);
            tableNode.setDatabaseName(this.databaseName);
            tableNode.setSchemaName(this.getName());
            tableNode.setAlias(alias);
            this.addChild(tableNode);
            return null;
        } else {
            TableNode tableNode = (TableNode) this.getChild(tableName);
            String oldAlias = tableNode.getAlias();
            tableNode.setAlias(alias);
            return oldAlias;
        }
    }
}
