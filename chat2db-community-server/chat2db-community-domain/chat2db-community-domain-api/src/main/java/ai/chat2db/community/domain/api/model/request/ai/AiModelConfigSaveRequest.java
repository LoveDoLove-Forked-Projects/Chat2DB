package ai.chat2db.community.domain.api.model.request.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiModelConfigSaveRequest {

    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String provider;

    @NotBlank
    private String model;

    private String apiKey;

    private String baseUrl;

    private String projectId;

    private String location;

    private Double temperature;

    private Integer maxTokens;

    private Boolean enabled = Boolean.TRUE;

    private Boolean defaultConfig = Boolean.FALSE;
}
