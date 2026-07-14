package ai.chat2db.community.domain.api.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EngineType {
    private String name;
    private boolean supportTTL;
    private boolean supportSortOrder;
    private boolean supportSkippingIndices;
    private boolean supportDeduplication;
    private boolean supportSettings;
    private boolean supportParallelInsert;
    private boolean supportProjections;
    private boolean supportReplication;

}
