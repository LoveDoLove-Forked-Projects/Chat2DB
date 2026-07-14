package ai.chat2db.community.domain.api.model.request.db;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbExecuteResultEnhanceRequest {

    @NotNull
    private ExecuteResponse executeResult;

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;
}
