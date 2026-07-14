package ai.chat2db.community.web.api.model.response.cli;

import lombok.Data;

@Data
public class CliDatabaseResponse {

    private String name;

    private String comment;

    private String charset;

    private String collation;

    private String owner;

    private Boolean system;
}
