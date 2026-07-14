package ai.chat2db.community.domain.api.model.sql;

import lombok.Data;


@Data
public class RefreshTarget {




    private Long dataSourceId;



    private String databaseName;



    private String schemaName;
    private String tableName;
}
