package ai.chat2db.community.domain.api.model.request.cli;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliConnectionResolveRequest extends CliPageQueryRequest {

    @NotNull
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;
}
