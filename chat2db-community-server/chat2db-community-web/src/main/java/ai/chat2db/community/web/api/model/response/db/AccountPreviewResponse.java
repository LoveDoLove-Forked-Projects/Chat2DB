package ai.chat2db.community.web.api.model.response.db;

import ai.chat2db.community.domain.api.enums.plugin.AccountActionTypeEnum;
import lombok.Data;

@Data
public class AccountPreviewResponse {
    private AccountActionTypeEnum actionType;
    private String sql;
    private String previewToken;
}
