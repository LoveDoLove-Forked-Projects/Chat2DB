package ai.chat2db.community.domain.api.model.parser.statement.create;

import ai.chat2db.community.domain.api.enums.parser.SqlTypeEnum;
import ai.chat2db.community.domain.api.model.parser.statement.Statement;
import ai.chat2db.community.domain.api.model.parser.token.ColumnDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateTableStatement extends Statement {


    private String databaseName;

    private String schemaName;

    private String tableName;

    private String tableComment;

    private List<ColumnDeclaration> columnDeclarations;


    public void addColumnDeclaration(ColumnDeclaration columnDeclaration) {
        if (Objects.isNull(this.columnDeclarations)) {
            this.columnDeclarations = new ArrayList<>(10);
        }
        columnDeclarations.add(columnDeclaration);
    }

    public List<ColumnDeclaration> getColumnDeclarations() {
        return columnDeclarations;
    }

    public void setColumnDeclarations(List<ColumnDeclaration> columnDeclarations) {
        this.columnDeclarations = columnDeclarations;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public CreateTableStatement() {
        this.type = SqlTypeEnum.CREATE_TABLE.name();
    }
}
