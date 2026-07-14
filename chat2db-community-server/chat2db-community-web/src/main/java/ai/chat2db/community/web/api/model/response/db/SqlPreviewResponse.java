package ai.chat2db.community.web.api.model.response.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlPreviewResponse {
    private String sql;
}
