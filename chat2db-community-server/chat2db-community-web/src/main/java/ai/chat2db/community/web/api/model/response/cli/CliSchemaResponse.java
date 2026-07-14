package ai.chat2db.community.web.api.model.response.cli;

import lombok.Data;

@Data
public class CliSchemaResponse {

    private String name;

    private String databaseName;

    private String comment;

    private String owner;

    private Boolean system;
}
