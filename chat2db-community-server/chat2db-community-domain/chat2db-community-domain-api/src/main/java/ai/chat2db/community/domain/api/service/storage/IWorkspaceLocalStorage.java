package ai.chat2db.community.domain.api.service.storage;

import ai.chat2db.community.domain.api.converter.LocalStorageConverter;

import java.util.List;

/**
 * Defines basic local collection storage operations.
 */
public interface IWorkspaceLocalStorage<T> {

    /**
     * Lists all records in this local storage collection.
     *
     * @return records in storage order.
     */
    List<T> getDataList();

    /**
     * Returns one record by identifier.
     *
     * @param id record identifier.
     * @return record, or null when no matching record exists.
     */
    T getById(Long id);

    /**
     * Saves a new record in this local storage collection.
     *
     * @param data record data to persist.
     * @return created record identifier.
     */
    Long save(T data);

    /**
     * Updates an existing record in this local storage collection.
     *
     * @param data record data to persist.
     */
    void update(T data);

    /**
     * Deletes a record from this local storage collection.
     *
     * @param id record identifier.
     */
    void delete(Long id);

    /**
     * Merges persisted and updated record values after save.
     *
     * @param before record state before update.
     * @param update record update values.
     * @return merged record state.
     */
    default T getAfterSave(T before, T update) {
        return LocalStorageConverter.mergeNotNullProperties(before, update);
    }
}
