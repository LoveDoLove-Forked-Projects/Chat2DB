package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiExecuteSqlRequest {

    @NotBlank
    private String sql;

    private Integer pageSize;

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    @Valid
    private AiToolContextRequest aiToolContextRequest;
}
