
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
public class Function {

    @JsonAlias({"FUNCTION_CAT"})
    private String databaseName;

    @JsonAlias({"FUNCTION_SCHEM"})
    private String schemaName;

    @JsonAlias({"FUNCTION_NAME"})
    private String functionName;

    @JsonAlias({"REMARKS"})
    private String remarks;

    @JsonAlias({"FUNCTION_TYPE"})
    private Short functionType;

    @JsonAlias({"SPECIFIC_NAME"})
    private String specificName;

    private String functionBody;

    private String functionTemplate;

}
