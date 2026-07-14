package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliIndex {

    private String name;

    private String tableName;

    private String type;

    private Boolean unique;

    private String comment;

    private String schemaName;

    private String databaseName;

    private String method;
}
