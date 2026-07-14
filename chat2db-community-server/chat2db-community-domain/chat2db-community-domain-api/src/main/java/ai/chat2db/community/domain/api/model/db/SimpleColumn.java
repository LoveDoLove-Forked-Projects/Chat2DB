package ai.chat2db.community.domain.api.model.db;

import ai.chat2db.community.domain.api.model.metadata.TableColumn;
import lombok.Data;


@Data
public class SimpleColumn {
    private String datasourceName;
    private String databaseName;
    private String schemaName;
    private String tableName;
    private String columnName;
    private String insertText;
    private String comment;
    private String dataType;


    public SimpleColumn(TableColumn tableColumn) {
        this.databaseName = tableColumn.getDatabaseName();
        this.schemaName = tableColumn.getSchemaName();
        this.tableName = tableColumn.getTableName();
        this.columnName = tableColumn.getName();
        this.comment = tableColumn.getComment();
        this.dataType = tableColumn.getColumnType();
    }

    public SimpleColumn() {

    }
}
