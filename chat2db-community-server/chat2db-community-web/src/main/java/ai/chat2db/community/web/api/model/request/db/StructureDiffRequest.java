package ai.chat2db.community.web.api.model.request.db;

import lombok.Data;

@Data
public class StructureDiffRequest {
    private StructureInfo source;
    private StructureInfo target;

    @Data
    public static class StructureInfo {
        private Long dataSourceId;
        private String databaseName;
        private String schemaName;
    }
}
