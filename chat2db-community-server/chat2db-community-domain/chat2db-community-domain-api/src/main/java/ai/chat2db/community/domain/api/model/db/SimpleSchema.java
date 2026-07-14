package ai.chat2db.community.domain.api.model.db;

import lombok.Data;


@Data
public class SimpleSchema {


    private String datasourceName;


    private String databaseName;


    private String schemaName;
    private String insertText;


}
