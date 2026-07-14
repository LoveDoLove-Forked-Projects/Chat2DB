package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliSqlColumn {

    private String name;

    private String type;

    private String tableName;
}
