package ai.chat2db.community.web.api.model.response.cli;

import lombok.Data;

@Data
public class CliIndexResponse {

    private String name;

    private String tableName;

    private String type;

    private Boolean unique;

    private String comment;

    private String schemaName;

    private String databaseName;

    private String method;
}
