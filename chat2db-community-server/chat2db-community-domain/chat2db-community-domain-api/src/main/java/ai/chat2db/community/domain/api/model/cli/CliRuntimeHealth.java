package ai.chat2db.community.domain.api.model.cli;

import lombok.Data;

@Data
public class CliRuntimeHealth {

    private Boolean ready;

    private String edition;

    private String version;

    private String apiVersion;
}
