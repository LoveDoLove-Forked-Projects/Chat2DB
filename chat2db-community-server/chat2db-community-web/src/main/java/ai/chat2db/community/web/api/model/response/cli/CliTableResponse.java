package ai.chat2db.community.web.api.model.response.cli;

import java.util.List;

import lombok.Data;

@Data
public class CliTableResponse {

    private String name;

    private String comment;

    private String schemaName;

    private String databaseName;

    private String type;

    private String engine;

    private Long rows;

    private List<CliColumnResponse> columns;

    private List<CliIndexResponse> indexes;
}
