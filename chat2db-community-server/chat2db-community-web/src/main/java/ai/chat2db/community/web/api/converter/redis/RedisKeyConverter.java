package ai.chat2db.community.web.api.converter.redis;

import ai.chat2db.community.domain.api.model.key.KeyCreate;
import ai.chat2db.community.domain.api.model.key.KeyDelete;
import ai.chat2db.community.domain.api.model.key.KeyRequest;
import ai.chat2db.community.domain.api.model.key.KeyUpdate;
import ai.chat2db.community.web.api.model.request.redis.KeyCreateRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyDeleteRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyDetailRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyQueryRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyScanRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class RedisKeyConverter {

    public abstract KeyCreate request2create(KeyCreateRequest request);

    @Mapping(source = "oldRedisKey", target = "oldKey")
    @Mapping(source = "newRedisKey", target = "newKey")
    public abstract KeyUpdate request2update(KeyUpdateRequest request);

    public abstract KeyDelete request2delete(KeyDeleteRequest request);

    @Mapping(source = "searchKey", target = "pattern")
    public abstract KeyRequest request2query(KeyQueryRequest request);

    @Mapping(source = "searchKey", target = "pattern")
    public abstract ai.chat2db.community.domain.api.model.key.KeyScanRequest request2scan(KeyScanRequest request);

    public abstract ai.chat2db.community.domain.api.model.key.KeyDetailRequest request2detail(KeyDetailRequest request);
}
