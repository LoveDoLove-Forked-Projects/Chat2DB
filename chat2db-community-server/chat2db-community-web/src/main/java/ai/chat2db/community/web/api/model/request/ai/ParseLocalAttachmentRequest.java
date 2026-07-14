package ai.chat2db.community.web.api.model.request.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParseLocalAttachmentRequest {

    @NotBlank
    private String filePath;

    private String fileName;
}
