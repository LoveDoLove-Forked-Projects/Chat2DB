package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.InputStream;

@Data
public class AiAttachmentParseRequest {

    @NotBlank
    private String fileName;

    @NotNull
    private InputStream inputStream;
}
