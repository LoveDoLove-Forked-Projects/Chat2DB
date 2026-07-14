package ai.chat2db.community.domain.api.model.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Type {

    @JsonAlias("TYPE_NAME")
    private String typeName;

    @JsonAlias("DATA_TYPE")
    private Integer dataType;

    @JsonAlias("PRECISION")
    private Integer precision;

    @JsonAlias("LITERAL_PREFIX")
    private String literalPrefix;

    @JsonAlias("LITERAL_SUFFIX")
    private String literalSuffix;

    @JsonAlias("CREATE_PARAMS")
    private String createParams;

    @JsonAlias("NULLABLE")
    private Short nullable;


    @JsonAlias("CASE_SENSITIVE")
    private Boolean caseSensitive;

    @JsonAlias("SEARCHABLE")
    private Short searchable;

    @JsonAlias("UNSIGNED_ATTRIBUTE")
    private Boolean unsignedAttribute;


    @JsonAlias("FIXED_PREC_SCALE")
    private Boolean fixedPrecScale;

    @JsonAlias("AUTO_INCREMENT")
    private Boolean autoIncrement;

    @JsonAlias("LOCAL_TYPE_NAME")
    private String localTypeName;

    @JsonAlias("MINIMUM_SCALE")
    private Short minimumScale;

    @JsonAlias("MAXIMUM_SCALE")
    private Short maximumScale;

    @JsonAlias("SQL_DATA_TYPE")
    private Integer sqlDataType;

    @JsonAlias("SQL_DATETIME_SUB")
    private Integer sqlDatetimeSub;


    @JsonAlias("NUM_PREC_RADIX")
    private Integer numPrecRadix;
}
