package ai.chat2db.community.domain.api.model.operation;

import ai.chat2db.community.domain.api.model.runtime.ConnectionProfile;
import ai.chat2db.community.tools.model.Context;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SqlOperationLogRecord {

    private String sql;

    private String status;

    private Long useTime;

    private Long operationRows;

    private String sqlType;

    private String errorMessage;

    private String executionId;

    private Integer statementSequence;

    private String source;

    private ConnectionProfile connectionProfile;

    private Context context;
}
