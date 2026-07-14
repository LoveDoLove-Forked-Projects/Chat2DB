package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.key.KeyCreate;
import ai.chat2db.community.domain.api.model.key.KeyDelete;
import ai.chat2db.community.domain.api.model.key.KeyDetailRequest;
import ai.chat2db.community.domain.api.model.key.KeyEntry;
import ai.chat2db.community.domain.api.model.key.KeyRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanRequest;
import ai.chat2db.community.domain.api.model.key.KeyScanResult;
import ai.chat2db.community.domain.api.model.key.KeyUpdate;
import java.sql.Connection;
import java.util.List;

/**
 * Manages dialect-specific key-value or security key metadata.
 */
public interface IKeyOperations {

    /**
     * Queries key entries that match the provided filter.
     *
     * @param connection active database connection.
     * @param keyRequest key query criteria.
     * @return matching key entries.
     */
    List<KeyEntry> query(Connection connection, KeyRequest keyRequest);

    /**
     * Scans key entries using cursor based iteration.
     *
     * @param connection active database connection.
     * @param keyRequest key scan criteria.
     * @return one scan batch and next cursor.
     */
    KeyScanResult scan(Connection connection, KeyScanRequest keyRequest);

    /**
     * Queries a full key entry by key name.
     *
     * @param connection active database connection.
     * @param keyRequest key detail criteria.
     * @return key entry detail.
     */
    KeyEntry keyDetail(Connection connection, KeyDetailRequest keyRequest);

    /**
     * Creates a key entry.
     *
     * @param connection active database connection.
     * @param command key creation command.
     * @return created key entry.
     */
    KeyEntry create(Connection connection, KeyCreate command);

    /**
     * Updates an existing key entry.
     *
     * @param connection active database connection.
     * @param command key update command.
     * @return updated key entry.
     */
    KeyEntry update(Connection connection, KeyUpdate command);

    /**
     * Deletes a key entry.
     *
     * @param connection active database connection.
     * @param command key deletion command.
     * <p>
     * Typical usage:
     */
    void delete(Connection connection, KeyDelete command);
}
