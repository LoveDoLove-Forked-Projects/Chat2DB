package ai.chat2db.community.domain.api.model.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class FunctionParameter {

    @JsonAlias("FUNCTION_CAT")
    private String functionCat;

    @JsonAlias("FUNCTION_SCHEM")
    private String functionSchem;

    @JsonAlias("FUNCTION_NAME")
    private String functionName;

    @JsonAlias("COLUMN_NAME")
    private String columnName;

    @JsonAlias("COLUMN_TYPE")
    private Integer columnType;

    @JsonAlias("DATA_TYPE")
    private Integer dataType;

    @JsonAlias("TYPE_NAME")
    private String typeName;

    @JsonAlias("PRECISION")
    private Integer precision;

    @JsonAlias("LENGTH")
    private Integer length;

    @JsonAlias("SCALE")
    private Integer scale;

    @JsonAlias("RADIX")
    private Integer radix;

    @JsonAlias("NULLABLE")
    private Integer nullable;

    @JsonAlias("REMARKS")
    private String remarks;

    @JsonAlias("CHAR_OCTET_LENGTH")
    private Integer charOctetLength;

    @JsonAlias("ORDINAL_POSITION")
    private Integer ordinalPosition;

    @JsonAlias("IS_NULLABLE")
    private String isNullable;

    @JsonAlias("SPECIFIC_NAME")
    private String specificName;
}
