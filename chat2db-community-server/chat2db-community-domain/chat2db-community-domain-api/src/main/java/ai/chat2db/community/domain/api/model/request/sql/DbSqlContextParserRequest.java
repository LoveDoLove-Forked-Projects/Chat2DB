package ai.chat2db.community.domain.api.model.request.sql;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DbSqlContextParserRequest {


    @NotNull
    private Long dataSourceId;


    private Long consoleId;


    private String databaseName;


    private String schemaName;


    @NotBlank
    private String sql;

}
