package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.key.KeyCreate;
import ai.chat2db.community.domain.api.model.key.KeyDelete;
import ai.chat2db.community.domain.api.model.key.KeyDetailRequest;
import ai.chat2db.community.domain.api.model.key.KeyEntry;
import ai.chat2db.community.domain.api.model.key.KeyRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanResult;
import ai.chat2db.community.domain.api.model.key.KeyUpdate;

import java.util.List;

public interface IDbRedisKeyService {

    /**
     * Creates a Redis key in the current connection scope.
     *
     * @param param Redis key creation parameters.
     * @return created key entry.
     */
    KeyEntry create(KeyCreate param);

    /**
     * Updates a Redis key in the current connection scope.
     *
     * @param param Redis key update parameters.
     * @return updated key entry.
     */
    KeyEntry update(KeyUpdate param);

    /**
     * Deletes a Redis key in the current connection scope.
     *
     * @param param Redis key deletion parameters.
     */
    void delete(KeyDelete param);

    /**
     * Queries Redis keys in the current connection scope.
     *
     * @param keyRequest Redis key query parameters.
     * @return matched key entries.
     */
    List<KeyEntry> query(KeyRequest keyRequest);

    /**
     * Scans Redis keys in the current connection scope.
     *
     * @param keyRequest Redis key scan parameters.
     * @return one cursor scan batch.
     */
    KeyScanResult scan(KeyScanRequest keyRequest);

    /**
     * Queries full Redis key detail in the current connection scope.
     *
     * @param keyRequest Redis key detail parameters.
     * @return key entry detail.
     */
    KeyEntry keyDetail(KeyDetailRequest keyRequest);
}
