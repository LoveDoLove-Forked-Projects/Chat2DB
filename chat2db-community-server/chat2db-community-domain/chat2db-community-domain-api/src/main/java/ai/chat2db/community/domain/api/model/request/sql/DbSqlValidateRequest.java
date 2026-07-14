package ai.chat2db.community.domain.api.model.request.sql;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbSqlValidateRequest {
    @NotBlank
    private String sql;
    private Long consoleId;
    @NotNull
    private Long dataSourceId;
    private String databaseName;
    private String schemaName;
}
