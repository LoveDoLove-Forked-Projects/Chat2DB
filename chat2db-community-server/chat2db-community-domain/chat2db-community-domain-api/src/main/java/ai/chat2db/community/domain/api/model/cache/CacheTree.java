package ai.chat2db.community.domain.api.model.cache;

import ai.chat2db.community.domain.api.model.datasource.DataSourceNode;
import ai.chat2db.community.domain.api.model.db.DatabaseNode;
import ai.chat2db.community.domain.api.model.db.SchemaNode;
import ai.chat2db.community.domain.api.model.db.TableNode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
@AllArgsConstructor
public class CacheTree implements Serializable {
    private static final long serialVersionUID = -1L;

    private DataSourceNode root;

    public static final String DEFAULT = "default";

    public CacheTree() {
        this.root = new DataSourceNode("DataSourceRoot");
    }

    private final HashMap<String, TableNode> aliasToTableNodeMap = new HashMap<>();

    public TableNode getTableNodeByAlias(String alias) {
        return aliasToTableNodeMap.get(alias);
    }


    public void addDataSource(String datasourceName, String datasourceId) {
        if (!root.hasChild(datasourceId)) {
            DataSourceNode dataSourceNode = new DataSourceNode(datasourceId);
            dataSourceNode.setDatasourceName(datasourceName);
            root.addChild(dataSourceNode);
        } else {
            DataSourceNode dataSourceNode = (DataSourceNode) root.getChild(datasourceId);
            dataSourceNode.setDatasourceName(datasourceName);
        }
    }
    public void addDatabase(String datasourceId, String dbName, boolean hasDatabase) {
        addDataSource(null, datasourceId);
        dbName = hasDatabase ? dbName : DEFAULT;
        DataSourceNode dataSourceNode = (DataSourceNode) root.getChild(datasourceId);
        dataSourceNode.addDatabase(dbName);
    }
    public void addSchema(String datasourceId,
                          String dbName, String schemaName,
                          boolean hasDatabase, boolean hasSchema) {
        dbName = hasDatabase ? dbName : DEFAULT;
        schemaName = hasSchema ? schemaName : DEFAULT;
        addDatabase(datasourceId, dbName, hasDatabase);
        DatabaseNode dbNode = (DatabaseNode) root.getChild(datasourceId).getChild(dbName);
        dbNode.addSchema(schemaName);
    }
    public void addTable(String dataSourceId, String dbName,
                         String schemaName, String tableName, String alias,
                         boolean hasDatabase, boolean hasSchema) {
        dbName = hasDatabase ? dbName : DEFAULT;
        schemaName = hasSchema ? schemaName : DEFAULT;
        addSchema(dataSourceId, dbName, schemaName, hasDatabase, hasSchema);
        SchemaNode schemaNode = (SchemaNode) root.getChild(dataSourceId).getChild(dbName).getChild(schemaName);
        String oldTableAlias = schemaNode.addTable(tableName, alias);
        if (StringUtils.isNotBlank(oldTableAlias)) {
            aliasToTableNodeMap.remove(oldTableAlias);
        }
        if (StringUtils.isNotBlank(alias)) {
            aliasToTableNodeMap.put(alias, (TableNode) schemaNode.getChild(tableName));
        }
    }

    public boolean tableExists(String dataSourceId, String dbName, String schemaName,
                               String tableName, String alias,
                               boolean hasDatabase, boolean hasSchema) {
        dbName = hasDatabase ? dbName : DEFAULT;
        schemaName = hasSchema ? schemaName : DEFAULT;
        DataSourceNode dataSourceNode = (DataSourceNode) root.getChild(dataSourceId);
        if (Objects.isNull(dataSourceNode)) {
            return false;
        }
        DatabaseNode databaseNode = (DatabaseNode) dataSourceNode.getChild(dbName);
        if (Objects.isNull(databaseNode)) {
            return false;
        }
        SchemaNode schemaNode = (SchemaNode) databaseNode.getChild(schemaName);
        if (Objects.isNull(schemaNode)) {
            return false;
        }
        if (schemaNode.hasChild(tableName)) {
            TableNode tableNode = (TableNode) schemaNode.getChild(tableName);
            String oldAlias = tableNode.getAlias();
            if (StringUtils.isNotBlank(oldAlias)) {
                aliasToTableNodeMap.remove(oldAlias);
            }
            tableNode.setAlias(alias);
            if (!aliasToTableNodeMap.containsKey(alias)) {
                if (StringUtils.isNotBlank(alias)) {
                    aliasToTableNodeMap.put(alias, tableNode);
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
