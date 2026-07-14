package ai.chat2db.community.domain.api.model.er;

import lombok.Data;

@Data
public class ERPosition {

    private Long id;
    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    private String position;

}
