package ai.chat2db.community.web.api.model.response.db;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseObjectDeletePrepareResponse {

    private String confirmName;

    private String sqlPreview;

    private String objectType;

    private String dbType;
}
