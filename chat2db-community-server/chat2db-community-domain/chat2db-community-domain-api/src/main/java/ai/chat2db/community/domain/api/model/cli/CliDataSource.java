package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliDataSource {

    private Long id;

    private String alias;

    private String dbType;

    private String host;

    private String port;

    private String user;

    private String url;

    private String database;

    private String environment;

    private Boolean supportDatabase;

    private Boolean supportSchema;
}
