package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliConnectionTestResponse {

    private String mode;

    private Long dataSourceId;

    private String dbType;

    private Boolean canConnect;

    private Long durationMs;

    private String errorCode;

    private String errorMessage;

    private String errorDetail;
}
