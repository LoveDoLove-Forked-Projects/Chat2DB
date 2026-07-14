package ai.chat2db.community.domain.api.model.request.er;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbErQueryRequest {

    @NotNull
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;


}
