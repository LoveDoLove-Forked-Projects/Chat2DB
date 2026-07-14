package ai.chat2db.community.domain.api.model.sql;

import ai.chat2db.community.domain.api.model.db.SimpleIdentifier;
import ai.chat2db.community.domain.api.model.db.SimpleInsertValueMapping;
import ai.chat2db.community.domain.api.model.db.SimpleTableColumnMapping;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlStatement {


    private String sql;
    private int sqlStartRowNum;
    private int sqlStartColNum;
    private int sqlEndRowNum;
    private int sqlEndColNum;



    private String type;



    private String statementType;


    private String comment;



    private List<SimpleIdentifier> identifiers;


    private List<SimpleTableColumnMapping> tableColumns;


    private List<SimpleInsertValueMapping> insertValueMappings;

}
