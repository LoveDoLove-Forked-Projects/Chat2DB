package ai.chat2db.community.domain.api.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Header {



    private String dataType;




    private String name;


    private Boolean primaryKey;


    private String comment;

    private String defaultValue;

    private Integer autoIncrement;

    private Integer nullable;

    private Integer columnSize;

    private Integer decimalDigits;

    private String columnType;

    private String editorType;

    private String columnName;

    private String tableName;
}
