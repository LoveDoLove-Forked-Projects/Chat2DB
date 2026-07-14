package ai.chat2db.community.domain.api.model.runtime;

import lombok.Data;

@Data
public class ConnectionProfile {

    private Long dataSourceId;

    private Long consoleId;

    private String databaseName;

    private String schemaName;

    private String dbType;

    private String alias;

    private String type;

    private String url;

    private String user;
}
