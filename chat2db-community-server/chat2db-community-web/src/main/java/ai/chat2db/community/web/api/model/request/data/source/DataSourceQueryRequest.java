package ai.chat2db.community.web.api.model.request.data.source;

import ai.chat2db.community.tools.wrapper.request.PageQueryRequest;
import lombok.Data;


@Data
public class DataSourceQueryRequest extends PageQueryRequest {


    private String searchKey;


    private Long organizationId;


    private boolean refresh;
}
