package ai.chat2db.community.web.api.model.request.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatSessionDeleteRequest {

    @NotBlank
    private String id;
}
