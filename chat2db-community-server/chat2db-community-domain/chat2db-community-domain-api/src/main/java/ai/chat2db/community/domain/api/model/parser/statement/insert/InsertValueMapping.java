package ai.chat2db.community.domain.api.model.parser.statement.insert;

import ai.chat2db.community.domain.api.enums.parser.InsertValueMappingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertValueMapping {

    private Token columnFirstToken;
    private Token columnLastToken;
    private Token valueFirstToken;
    private Token valueLastToken;
    private Token rowFirstToken;
    private Token rowLastToken;
    private int rowIndex;
    private int columnIndex;
    private InsertValueMappingStatusEnum mappingStatus;

    public InsertValueMapping(Token columnFirstToken, Token columnLastToken,
                              Token valueFirstToken, Token valueLastToken,
                              int rowIndex, int columnIndex,
                              InsertValueMappingStatusEnum mappingStatus) {
        this.columnFirstToken = columnFirstToken;
        this.columnLastToken = columnLastToken;
        this.valueFirstToken = valueFirstToken;
        this.valueLastToken = valueLastToken;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.mappingStatus = mappingStatus;
    }
}
