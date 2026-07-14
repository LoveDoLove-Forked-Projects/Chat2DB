package ai.chat2db.community.domain.api.model.db;

import lombok.Data;


@Data
public class SimpleDatabase {


    private String datasourceName;


    private String databaseName;
    private String insertText;

}
