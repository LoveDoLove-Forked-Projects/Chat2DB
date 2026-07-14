package ai.chat2db.community.domain.api.model.ai;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiChatSession {

    private String id;

    private Long userId;

    private String title;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
