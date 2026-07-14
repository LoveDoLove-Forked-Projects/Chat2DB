package ai.chat2db.community.web.api.model.request.db;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineMigrationRequest extends RoutineOperationRequest {

    @NotBlank
    private String ddl;
}
