package ai.chat2db.community.web.api.model.request.operation.saved;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class BatchTabCloseRequest {


    @NotNull
    private List<Long> idList;


    private Long organizationId;
}
