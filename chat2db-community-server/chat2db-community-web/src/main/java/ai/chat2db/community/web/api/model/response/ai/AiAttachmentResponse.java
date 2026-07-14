package ai.chat2db.community.web.api.model.response.ai;

import lombok.Data;

@Data
public class AiAttachmentResponse {

    private String fileName;

    private String fileType;

    private String contentCategory;

    private String content;

    private Integer contentLength;

    private Boolean truncated;
}
