package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;

@Data
public class DatabaseCreateRequest extends DataSourceBaseRequest {

    private String name;

    private String comment;

    private String charset;

    private String collation;
}
