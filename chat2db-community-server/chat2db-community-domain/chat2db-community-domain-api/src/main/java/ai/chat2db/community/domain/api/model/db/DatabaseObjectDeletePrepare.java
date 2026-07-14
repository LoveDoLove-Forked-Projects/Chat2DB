package ai.chat2db.community.domain.api.model.db;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseObjectDeletePrepare {

    private String confirmName;

    private String sqlPreview;

    private String objectType;

    private String dbType;
}
