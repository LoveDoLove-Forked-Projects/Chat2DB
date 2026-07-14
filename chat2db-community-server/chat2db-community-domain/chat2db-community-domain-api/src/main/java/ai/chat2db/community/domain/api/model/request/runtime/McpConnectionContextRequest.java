package ai.chat2db.community.domain.api.model.request.runtime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class McpConnectionContextRequest {

    @NotBlank
    private String dbType;

    @NotBlank
    private String jdbcUrl;

    private String user;

    private String password;

    private String databaseName;

    private String schemaName;
}
