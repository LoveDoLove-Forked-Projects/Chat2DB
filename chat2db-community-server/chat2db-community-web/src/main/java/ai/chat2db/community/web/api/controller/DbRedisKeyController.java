package ai.chat2db.community.web.api.controller;

import ai.chat2db.community.tools.wrapper.result.ActionResult;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.tools.wrapper.result.ListResult;
import ai.chat2db.community.web.api.aspect.connection.ConnectionInfoAspect;
import ai.chat2db.community.web.api.converter.redis.RedisKeyConverter;
import ai.chat2db.community.web.api.model.request.redis.KeyCreateRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyDeleteRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyDetailRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyQueryRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyScanRequest;
import ai.chat2db.community.web.api.model.request.redis.KeyUpdateRequest;
import ai.chat2db.community.domain.api.model.key.KeyEntry;
import ai.chat2db.community.domain.api.model.key.KeyScanResult;
import ai.chat2db.community.domain.api.service.db.IDbRedisKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Manages Redis key browsing and key-value operations.
 */
@RequestMapping("/api/redis")
@RestController
@ConnectionInfoAspect
public class DbRedisKeyController {

    @Autowired
    private RedisKeyConverter redisKeyConverter;

    @Autowired
    private IDbRedisKeyService redisKeyService;

    /**
     * Creates Redis keys.
     * <p>
     * Endpoint: {@code POST /api/redis/create}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing key entry.
     */
    @PostMapping("/create")
    public DataResult<KeyEntry> create(@RequestBody KeyCreateRequest request) {
        return DataResult.of(redisKeyService.create(redisKeyConverter.request2create(request)));
    }

    /**
     * Updates Redis keys.
     * <p>
     * Endpoint: {@code POST /api/redis/update}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing key entry.
     */
    @PostMapping("/update")
    public DataResult<KeyEntry> update(@RequestBody KeyUpdateRequest request) {
        return DataResult.of(redisKeyService.update(redisKeyConverter.request2update(request)));
    }

    /**
     * Deletes Redis keys.
     * <p>
     * Endpoint: {@code POST /api/redis/delete}.
     *
     * @param request request payload or query parameters for the operation.
     * @return operation result for the request.
     */
    @PostMapping("/delete")
    public ActionResult delete(@RequestBody KeyDeleteRequest request) {
        redisKeyService.delete(redisKeyConverter.request2delete(request));
        return ActionResult.isSuccess();
    }

    /**
     * Queries Redis keys.
     * <p>
     * Endpoint: {@code GET /api/redis/query}.
     *
     * @param request request payload or query parameters for the operation.
     * @return list result containing key entry.
     */
    @GetMapping("/query")
    public ListResult<KeyEntry> query(KeyQueryRequest request) {
        return ListResult.of(redisKeyService.query(redisKeyConverter.request2query(request)));
    }

    /**
     * Scans Redis keys.
     * <p>
     * Endpoint: {@code GET /api/redis/keys}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing one scan batch.
     */
    @GetMapping("/keys")
    public DataResult<KeyScanResult> keys(KeyScanRequest request) {
        return DataResult.of(redisKeyService.scan(redisKeyConverter.request2scan(request)));
    }

    /**
     * Queries Redis key detail.
     * <p>
     * Endpoint: {@code GET /api/redis/key_detail}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing key entry detail.
     */
    @GetMapping("/key_detail")
    public DataResult<KeyEntry> keyDetail(KeyDetailRequest request) {
        return DataResult.of(redisKeyService.keyDetail(redisKeyConverter.request2detail(request)));
    }
}
