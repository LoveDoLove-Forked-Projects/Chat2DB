package ai.chat2db.community.web.api.model.request.task;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;

@Data
public class SqlFileImportRequest extends DataSourceBaseRequest {

    private String fileName;

}
