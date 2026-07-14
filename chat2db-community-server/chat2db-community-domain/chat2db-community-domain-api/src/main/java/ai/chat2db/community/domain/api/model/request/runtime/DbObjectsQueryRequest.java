package ai.chat2db.community.domain.api.model.request.runtime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DbObjectsQueryRequest {

    private String databaseName;

    private String schemaName;

    @NotBlank
    private String objectName;

    private String objectType;
}
