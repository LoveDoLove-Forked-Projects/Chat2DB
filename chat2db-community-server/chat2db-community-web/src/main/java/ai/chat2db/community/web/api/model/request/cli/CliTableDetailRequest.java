package ai.chat2db.community.web.api.model.request.cli;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CliTableDetailRequest extends CliConnectionRequest {

    @NotBlank
    private String tableName;
}
