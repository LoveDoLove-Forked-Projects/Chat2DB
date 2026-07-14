package ai.chat2db.community.web.api.model.request.ai;

import ai.chat2db.community.domain.api.enums.ai.AiProviderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModelConfigSaveRequest {

    private String id;

    @NotBlank
    private String name;

    @NotNull
    private AiProviderEnum provider;

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
