package ai.chat2db.community.domain.api.model.request.datasource;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbDataSourcePositionUpdateRequest {

    @NotNull
    private Long namespaceId;

    @NotNull
    private Long dataSourceId;

    private Long beforePosition;

    private Long afterPosition;
}
