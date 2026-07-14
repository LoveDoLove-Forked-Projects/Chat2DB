package ai.chat2db.community.domain.api.model.ai;

import lombok.Data;

@Data
public class ChatAttachment {

    private String fileName;

    private String fileType;

    private String contentCategory;

    private String content;

    private Integer contentLength;

    private Boolean truncated;
}
