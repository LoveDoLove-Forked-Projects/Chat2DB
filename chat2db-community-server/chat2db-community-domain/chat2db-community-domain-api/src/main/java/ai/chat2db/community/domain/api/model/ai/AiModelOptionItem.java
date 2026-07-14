package ai.chat2db.community.domain.api.model.ai;

import lombok.Data;

@Data
public class AiModelOptionItem {

    private String value;

    private String label;

    private String provider;

    private String model;

    private String modelConfigId;

    private Boolean customOption;

    private Boolean defaultOption;
}
