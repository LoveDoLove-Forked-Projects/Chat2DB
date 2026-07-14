package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiBusinessContextBuildRequest {

    @NotNull
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;
}
