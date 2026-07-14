package ai.chat2db.community.web.api.model.request.operation.saved;

import ai.chat2db.community.tools.wrapper.request.PageQueryRequest;
import lombok.Data;


@Data
public class OperationQueryRequest extends PageQueryRequest{


    private Long dataSourceId;


    private String databaseName;


    private String searchKey;


    private String tabOpened;


    private String status;


    private Boolean orderByDesc;


    private String operationType;


    private Long organizationId;
}
