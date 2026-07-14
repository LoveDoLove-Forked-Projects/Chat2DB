package ai.chat2db.community.domain.api.model.account;

import lombok.Data;

@Data
public class AccountPreview {
    private String actionType;
    private String sql;
    private String previewToken;
}
