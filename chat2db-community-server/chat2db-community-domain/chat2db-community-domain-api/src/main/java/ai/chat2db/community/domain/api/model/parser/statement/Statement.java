package ai.chat2db.community.domain.api.model.parser.statement;

import ai.chat2db.community.domain.api.model.parser.info.ColumnInfo;
import ai.chat2db.community.domain.api.model.parser.info.TableInfo;
import ai.chat2db.community.domain.api.model.parser.node.SimpleNode;
import ai.chat2db.community.domain.api.model.parser.statement.insert.InsertValueMapping;
import ai.chat2db.community.domain.api.enums.parser.InsertValueMappingStatusEnum;
import ai.chat2db.community.domain.api.model.parser.token.Identifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.antlr.v4.runtime.Token;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Statement extends SimpleNode {
    protected String sql;
    protected String originalSql;
    protected String type;
    protected String statementType;
    protected List<Identifier> identifiers = new ArrayList<>(10);
    protected String comment;
    protected List<InsertValueMapping> insertValueMappings = new ArrayList<>(10);

    private Map<TableInfo, String> tableAliasMap;

    private Map<ColumnInfo, String> columnAliasMap;


    public void setType(String type) {
        if (Objects.isNull(this.type)) {
            this.type = type;
        }
    }


    public Statement(String sql) {
        this.sql = sql;
    }

    public void addIdentifier(Identifier identifier) {
        this.identifiers.add(identifier);
    }




    public void addIdentifier(String identifierName, String identifierType, String identifierAlias, Token firstToken, Token lastToken) {
        Identifier identifier = new Identifier(identifierName, identifierType, identifierAlias, firstToken, lastToken);
        this.identifiers.add(identifier);
    }




    public void addIdentifier(String identifierName, String identifierType, Token firstToken) {
        Identifier identifier = new Identifier(identifierName, identifierType, firstToken);
        this.identifiers.add(identifier);
    }

    public void addInsertValueMapping(Token columnFirstToken, Token columnLastToken,
                                      Token valueFirstToken, Token valueLastToken,
                                      int rowIndex, int columnIndex) {
        addInsertValueMapping(columnFirstToken, columnLastToken, valueFirstToken, valueLastToken,
                null, null, rowIndex, columnIndex);
    }

    public void addInsertValueMapping(Token columnFirstToken, Token columnLastToken,
                                      Token valueFirstToken, Token valueLastToken,
                                      Token rowFirstToken, Token rowLastToken,
                                      int rowIndex, int columnIndex) {
        addInsertValueMapping(columnFirstToken, columnLastToken, valueFirstToken, valueLastToken,
                rowFirstToken, rowLastToken, rowIndex, columnIndex, InsertValueMappingStatusEnum.MATCHED);
    }

    public void addUnmappedInsertColumn(Token columnFirstToken, Token columnLastToken,
                                        int rowIndex, int columnIndex) {
        addUnmappedInsertColumn(columnFirstToken, columnLastToken, null, null, rowIndex, columnIndex);
    }

    public void addUnmappedInsertColumn(Token columnFirstToken, Token columnLastToken,
                                        Token rowFirstToken, Token rowLastToken,
                                        int rowIndex, int columnIndex) {
        addInsertValueMapping(columnFirstToken, columnLastToken, null, null,
                rowFirstToken, rowLastToken, rowIndex, columnIndex, InsertValueMappingStatusEnum.UNMAPPED_COLUMN);
    }

    public void addUnmappedInsertValue(Token valueFirstToken, Token valueLastToken,
                                       int rowIndex, int columnIndex) {
        addUnmappedInsertValue(valueFirstToken, valueLastToken, null, null, rowIndex, columnIndex);
    }

    public void addUnmappedInsertValue(Token valueFirstToken, Token valueLastToken,
                                       Token rowFirstToken, Token rowLastToken,
                                       int rowIndex, int columnIndex) {
        addInsertValueMapping(null, null, valueFirstToken, valueLastToken,
                rowFirstToken, rowLastToken, rowIndex, columnIndex, InsertValueMappingStatusEnum.UNMAPPED_VALUE);
    }

    private void addInsertValueMapping(Token columnFirstToken, Token columnLastToken,
                                       Token valueFirstToken, Token valueLastToken,
                                       Token rowFirstToken, Token rowLastToken,
                                       int rowIndex, int columnIndex,
                                       InsertValueMappingStatusEnum mappingStatus) {
        InsertValueMapping insertValueMapping = new InsertValueMapping(
                columnFirstToken,
                columnLastToken,
                valueFirstToken,
                valueLastToken,
                rowIndex,
                columnIndex,
                mappingStatus);
        insertValueMapping.setRowFirstToken(rowFirstToken);
        insertValueMapping.setRowLastToken(rowLastToken);
        this.insertValueMappings.add(insertValueMapping);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statement statement = (Statement) o;
        if (this.firstToken == null || statement.firstToken == null) {
            return false;
        }
        if (this.firstToken.getLine() != statement.firstToken.getLine() ||
                this.firstToken.getCharPositionInLine() != statement.firstToken.getCharPositionInLine()) {
            return false;
        }
        return Objects.equals(sql, statement.sql);
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + (firstToken != null ? firstToken.getLine() : 0);
        result = 31 * result + (firstToken != null ? firstToken.getCharPositionInLine() : 0);
        result = 31 * result + (sql != null ? sql.hashCode() : 0);
        return result;
    }

}
