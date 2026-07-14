package ai.chat2db.community.domain.api.model.parser.info;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableInfo {
    private String database;
    private String schema;
    private String table;
}
