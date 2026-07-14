package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiLocalAttachmentParseRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private String filePath;
}
