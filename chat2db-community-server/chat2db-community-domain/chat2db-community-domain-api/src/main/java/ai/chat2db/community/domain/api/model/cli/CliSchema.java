package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliSchema {

    private String name;

    private String databaseName;

    private String comment;

    private String owner;

    private Boolean system;
}
