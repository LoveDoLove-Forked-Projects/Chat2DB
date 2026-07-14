package ai.chat2db.community.web.api.model.request.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModelConfigDeleteRequest {

    @NotBlank
    private String id;
}
