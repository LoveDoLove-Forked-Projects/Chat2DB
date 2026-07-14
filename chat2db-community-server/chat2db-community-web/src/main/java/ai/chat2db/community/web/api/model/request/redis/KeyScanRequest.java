package ai.chat2db.community.web.api.model.request.redis;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import lombok.Data;

@Data
public class KeyScanRequest extends DataSourceBaseRequest {

    private String searchKey;

    private String cursor;

    private Integer count;
}
