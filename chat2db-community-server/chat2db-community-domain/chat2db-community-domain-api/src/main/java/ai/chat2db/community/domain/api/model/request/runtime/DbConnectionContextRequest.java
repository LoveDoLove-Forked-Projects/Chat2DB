package ai.chat2db.community.domain.api.model.request.runtime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbConnectionContextRequest {

    @NotNull
    private Long dataSourceId;

    private Long consoleId;

    private String databaseName;

    private String schemaName;
}
