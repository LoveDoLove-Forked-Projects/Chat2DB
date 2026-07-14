package ai.chat2db.community.domain.api.model.cli;

import java.util.List;

import lombok.Data;

@Data
public class CliTable {

    private String name;

    private String comment;

    private String schemaName;

    private String databaseName;

    private String type;

    private String engine;

    private Long rows;

    private List<CliColumn> columns;

    private List<CliIndex> indexes;
}
