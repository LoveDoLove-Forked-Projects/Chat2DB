package ai.chat2db.community.domain.api.model.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ProcedureParameter {

    @JsonAlias("PROCEDURE_CAT")
    private String procedureCat;

    @JsonAlias("PROCEDURE_SCHEM")
    private String procedureSchem;

    @JsonAlias("PROCEDURE_NAME")
    private String procedureName;

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

    @JsonAlias("COLUMN_DEF")
    private String columnDef;

    @JsonAlias("SQL_DATA_TYPE")
    private Integer sqlDataType;

    @JsonAlias("SQL_DATETIME_SUB")
    private Integer sqlDatetimeSub;

    @JsonAlias("CHAR_OCTET_LENGTH")
    private Integer charOctetLength;

    @JsonAlias("ORDINAL_POSITION")
    private Integer ordinalPosition;

    @JsonAlias("IS_NULLABLE")
    private String isNullable;

    @JsonAlias("SPECIFIC_NAME")
    private String specificName;


}
