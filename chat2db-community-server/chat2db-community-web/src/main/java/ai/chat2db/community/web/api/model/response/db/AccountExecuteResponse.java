package ai.chat2db.community.web.api.model.response.db;

import ai.chat2db.community.domain.api.enums.plugin.AccountActionTypeEnum;
import lombok.Data;

@Data
public class AccountExecuteResponse {
    private AccountActionTypeEnum actionType;
    private String sql;
    private Boolean success;
    private String message;
    private String failureCode;
    private Integer errorCode;
    private String sqlState;
}
