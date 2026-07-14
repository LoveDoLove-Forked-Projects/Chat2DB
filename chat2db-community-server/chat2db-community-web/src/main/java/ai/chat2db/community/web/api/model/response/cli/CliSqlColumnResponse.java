package ai.chat2db.community.web.api.model.response.cli;

import lombok.Data;

@Data
public class CliSqlColumnResponse {

    private String name;

    private String type;

    private String tableName;
}
