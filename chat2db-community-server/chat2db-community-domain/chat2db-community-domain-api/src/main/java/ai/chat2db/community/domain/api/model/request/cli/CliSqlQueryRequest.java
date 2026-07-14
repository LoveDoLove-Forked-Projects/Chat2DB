package ai.chat2db.community.domain.api.model.request.cli;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliSqlQueryRequest extends CliConnectionResolveRequest {

    @NotBlank
    private String sql;

    private Long timeoutMs;

    private Integer resultSetId;

    private Boolean includeRowNumber = Boolean.TRUE;
}
