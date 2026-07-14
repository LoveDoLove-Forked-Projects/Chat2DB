package ai.chat2db.community.web.api.model.request.data.source;

import lombok.Data;

@Data
public class UpdateDatasourcePositionRequest {

    private Long namespaceId;

    private Long beforePosition;

    private Long afterPosition;

    private Long dataSourceId;
}
