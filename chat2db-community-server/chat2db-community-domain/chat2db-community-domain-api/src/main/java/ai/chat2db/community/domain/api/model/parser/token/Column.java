package ai.chat2db.community.domain.api.model.parser.token;

import lombok.Data;

@Data
public class Column {

    private String columnName;




    private String displayColumnType;
    private String columnType;
    private String columnPrecious;
    private String columnScale;
    private String columnAlias;


}
