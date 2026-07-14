package ai.chat2db.community.web.api.model.request.ai;

import ai.chat2db.community.domain.api.model.ai.ChatAttachment;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatMessage {

    @NotBlank
    private String role;

    @NotBlank
    private String content;

    private List<ChatAttachment> attachments = new ArrayList<>();
}
