package ai.chat2db.community.domain.api.model.account;

import lombok.Data;

@Data
public class AccountInfo {
    private String user;
    private String host;
    private String displayName;
    private String authenticationPlugin;
    private Boolean locked;
}
