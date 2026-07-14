package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class AiListTablesRequest {

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    @Valid
    private AiToolContextRequest aiToolContextRequest;
}
