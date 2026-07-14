package ai.chat2db.community.domain.api.model.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlHover {


    private String databaseName;


    private String schemaName;


    private String tableName;


    private String datasourceName;


    private String viewName;


    private String triggerName;


    private String ddl;


    private String comment;
    private String dataType;
    private String columnName;
}

