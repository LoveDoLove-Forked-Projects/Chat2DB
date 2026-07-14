package ai.chat2db.community.domain.api.model.account;

import lombok.Data;

@Data
public class AccountExecuteResponse {
    private String actionType;
    private String sql;
    private Boolean success;
    private String message;
    private String failureCode;
    private Integer errorCode;
    private String sqlState;
}
