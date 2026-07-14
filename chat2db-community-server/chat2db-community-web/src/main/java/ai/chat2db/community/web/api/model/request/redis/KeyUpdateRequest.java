package ai.chat2db.community.web.api.model.request.redis;

import ai.chat2db.community.web.api.model.request.data.source.DataSourceBaseRequest;
import ai.chat2db.community.domain.api.model.key.KeyEntry;

import lombok.Data;


@Data
public class KeyUpdateRequest extends DataSourceBaseRequest {

    private KeyEntry oldRedisKey;

    private KeyEntry newRedisKey;
}
