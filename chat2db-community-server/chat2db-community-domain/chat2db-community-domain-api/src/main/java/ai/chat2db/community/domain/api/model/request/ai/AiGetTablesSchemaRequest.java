package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AiGetTablesSchemaRequest {

    @NotEmpty
    private List<String> tableNames;

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    @Valid
    private AiToolContextRequest aiToolContextRequest;
}
