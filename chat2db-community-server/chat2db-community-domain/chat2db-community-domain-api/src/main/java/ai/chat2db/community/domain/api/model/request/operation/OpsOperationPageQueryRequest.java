package ai.chat2db.community.domain.api.model.request.operation;

import ai.chat2db.community.tools.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.Size;

import lombok.Data;


@Data
public class OpsOperationPageQueryRequest extends PageQueryParam {


    private Long dataSourceId;


    private String databaseName;


    private String status;


    @Size(max = 256)
    private String searchKey;


    private String tabOpened;


    private boolean orderByDesc;


    private String operationType;


    private Long userId;
}
