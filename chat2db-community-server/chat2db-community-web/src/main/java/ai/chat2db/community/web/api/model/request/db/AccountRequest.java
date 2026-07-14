package ai.chat2db.community.web.api.model.request.db;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountRequest extends DataSourceBaseRequest {
    private String user;
    private String host;
}
