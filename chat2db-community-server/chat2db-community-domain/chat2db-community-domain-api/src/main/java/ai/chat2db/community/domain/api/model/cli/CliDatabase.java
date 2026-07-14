package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliDatabase {

    private String name;

    private String comment;

    private String charset;

    private String collation;

    private String owner;

    private Boolean system;
}
