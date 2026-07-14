package ai.chat2db.community.domain.api.model.request.cli;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliTableDetailRequest extends CliConnectionResolveRequest {

    @NotBlank
    private String tableName;
}
