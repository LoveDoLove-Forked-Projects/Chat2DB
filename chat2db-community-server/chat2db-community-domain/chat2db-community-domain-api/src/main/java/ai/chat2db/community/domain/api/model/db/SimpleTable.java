package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.metadata.Table;
import lombok.Data;


@Data
public class SimpleTable {


    private String datasourceName;
    private String databaseName;
    private String schemaName;


    private String tableName;
    private String insertText;


    private String tableAlias;
    private String comment;

    public SimpleTable(Table table) {
        this.databaseName = table.getDatabaseName();
        this.schemaName = table.getSchemaName();
        this.tableName = table.getName();
        this.comment = table.getComment();
    }

    public SimpleTable(Table table, String insertText) {
        this.databaseName = table.getDatabaseName();
        this.schemaName = table.getSchemaName();
        this.tableName = table.getName();
        this.comment = table.getComment();
        this.insertText = insertText;
    }


    public SimpleTable() {
    }


}
