package ai.chat2db.community.domain.api.model.key;

import lombok.Data;

@Data
public class KeyDelete {

    private String databaseName;

    private String schemaName;

    private String keyName;
}
