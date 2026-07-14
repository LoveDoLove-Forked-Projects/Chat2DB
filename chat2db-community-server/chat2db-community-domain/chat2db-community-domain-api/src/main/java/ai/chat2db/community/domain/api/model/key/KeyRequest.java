package ai.chat2db.community.domain.api.model.key;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KeyRequest {

    @Size(max = 128)
    private String databaseName;

    private String schemaName;

    private String pattern;
}
