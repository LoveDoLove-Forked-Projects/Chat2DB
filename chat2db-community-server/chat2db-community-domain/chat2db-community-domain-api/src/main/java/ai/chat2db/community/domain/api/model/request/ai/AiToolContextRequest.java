package ai.chat2db.community.domain.api.model.request.ai;

import ai.chat2db.community.domain.api.model.runtime.ConnectionProfile;
import ai.chat2db.community.tools.model.Context;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class AiToolContextRequest {

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    @Valid
    private ConnectionProfile connectionProfile;

    @Valid
    private Context requestContext;
}
