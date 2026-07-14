package ai.chat2db.community.domain.api.model.ai;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiModelCatalogItem {

    private String provider;

    private List<String> models = new ArrayList<>();
}
