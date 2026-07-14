package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.cache.CacheNode;
import lombok.Data;


@Data
public class TableNode extends CacheNode {


    private String databaseName;
    private String schemaName;
    private String alias;

    public TableNode(String name) {
        super(name);
    }

}
