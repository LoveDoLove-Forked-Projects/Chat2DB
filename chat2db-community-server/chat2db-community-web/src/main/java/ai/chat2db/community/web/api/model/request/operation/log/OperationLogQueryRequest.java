package ai.chat2db.community.web.api.model.request.operation.log;

import ai.chat2db.community.tools.wrapper.request.PageQueryRequest;
import lombok.Data;


@Data
public class OperationLogQueryRequest extends PageQueryRequest {


    private String searchKey;


    private Long dataSourceId;


    private String databaseName;


    private String schemaName;


    private Long organizationId;


    private String operationType;
}
