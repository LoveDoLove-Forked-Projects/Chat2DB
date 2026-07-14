package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.metadata.Table;
import lombok.Data;


@Data
public class SimpleView {


    private String datasourceName;
    private String databaseName;
    private String schemaName;


    private String viewName;
    private String insertText;


    public SimpleView() {
    }


    public SimpleView(Table table) {
        this.databaseName=table.getDatabaseName();
        this.schemaName=table.getSchemaName();
        this.viewName=table.getName();
    }
    public SimpleView(Table table,String insertText) {
        this.databaseName=table.getDatabaseName();
        this.schemaName=table.getSchemaName();
        this.viewName=table.getName();
        this.insertText = insertText;
    }

}
