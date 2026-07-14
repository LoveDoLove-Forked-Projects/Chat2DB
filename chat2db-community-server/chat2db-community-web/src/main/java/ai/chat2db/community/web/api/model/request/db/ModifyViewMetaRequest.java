package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;

@Data
public class ModifyViewMetaRequest extends DataSourceBaseRequest {


    private String viewName;
}
