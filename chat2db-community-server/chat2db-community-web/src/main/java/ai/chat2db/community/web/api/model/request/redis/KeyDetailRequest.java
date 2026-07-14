package ai.chat2db.community.web.api.model.request.redis;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;

@Data
public class KeyDetailRequest extends DataSourceBaseRequest {

    private String keyName;
}
