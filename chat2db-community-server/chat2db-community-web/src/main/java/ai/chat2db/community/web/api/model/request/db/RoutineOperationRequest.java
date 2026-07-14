package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineOperationRequest extends DataSourceBaseRequest {

    @NotBlank
    private String routineType;

    @NotBlank
    private String routineName;
}
