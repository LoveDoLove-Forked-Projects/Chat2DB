package ai.chat2db.community.web.api.model.request.cli;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CliDatasourceDeleteRequest {

    @NotNull
    private Long dataSourceId;
}
