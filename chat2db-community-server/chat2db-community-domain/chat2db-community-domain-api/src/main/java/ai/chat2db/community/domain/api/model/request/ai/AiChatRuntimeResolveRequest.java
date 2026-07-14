package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AiChatRuntimeResolveRequest {

    private String modelConfigId;

    private String provider;

    private String model;

    private String apiKey;

    private String baseUrl;

    private String projectId;

    private String location;

    private Double temperature;

    @Min(1)
    private Integer maxTokens;
}
