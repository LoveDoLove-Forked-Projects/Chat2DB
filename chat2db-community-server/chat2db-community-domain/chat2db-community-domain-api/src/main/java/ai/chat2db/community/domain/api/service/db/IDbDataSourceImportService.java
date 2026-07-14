package ai.chat2db.community.domain.api.service.db;

/**
 * Imports datasource definitions from legacy local Chat2DB storage.
 */
public interface IDbDataSourceImportService {

    /**
     * Imports datasource definitions from the local community H2 database.
     */
    void importCommunityDataSources();
}
