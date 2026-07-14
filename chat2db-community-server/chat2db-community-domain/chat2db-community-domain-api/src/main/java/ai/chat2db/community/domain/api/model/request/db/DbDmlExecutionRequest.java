package ai.chat2db.community.domain.api.model.request.db;

import lombok.Data;

@Data
public class DbDmlExecutionRequest {

    private DbDlExecuteRequest executeRequest;

    private String source;
}
