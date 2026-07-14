
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
public class Procedure {

    @JsonAlias({"PROCEDURE_CAT"})
    private String databaseName;

    @JsonAlias({"PROCEDURE_SCHEM"})

    private String schemaName;

    @JsonAlias({"PROCEDURE_NAME"})
    private String procedureName;

    @JsonAlias({"REMARKS"})
    private String remarks;

    @JsonAlias({"PROCEDURE_TYPE"})

    private Short procedureType;

    @JsonAlias({"SPECIFIC_NAME"})
    private String specificName;

    private String procedureBody;
}
