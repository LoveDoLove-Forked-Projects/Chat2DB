package ai.chat2db.community.domain.api.service.cli;

import ai.chat2db.community.domain.api.model.cli.CliConnectionTestResponse;
import ai.chat2db.community.domain.api.model.cli.CliDataSource;
import ai.chat2db.community.domain.api.model.cli.CliPage;
import ai.chat2db.community.domain.api.model.request.cli.CliConnectionTestRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceCreateRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceListRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliDataSourceUpdateRequest;

/**
 * Exposes datasource operations for CLI callers.
 */
public interface ICliDataSourceService {

    /**
     * Lists datasources available to CLI callers.
     *
     * @param cliDataSourceListRequest CLI datasource listing parameters.
     * @return paged CLI datasource entries.
     */
    CliPage<CliDataSource> list(CliDataSourceListRequest cliDataSourceListRequest);

    /**
     * Returns one datasource for CLI callers.
     *
     * @param dataSourceId datasource identifier.
     * @return CLI datasource entry, or null when no matching datasource exists.
     */
    CliDataSource get(Long dataSourceId);

    /**
     * Tests a CLI datasource connection without persisting changes.
     *
     * @param cliConnectionTestRequest CLI connection test parameters.
     * @return connection test response.
     */
    CliConnectionTestResponse connectionTest(CliConnectionTestRequest cliConnectionTestRequest);

    /**
     * Creates a datasource from CLI input.
     *
     * @param cliDataSourceCreateRequest CLI datasource creation parameters.
     * @return created CLI datasource entry.
     */
    CliDataSource create(CliDataSourceCreateRequest cliDataSourceCreateRequest);

    /**
     * Updates a datasource from CLI input.
     *
     * @param cliDataSourceUpdateRequest CLI datasource update parameters.
     * @return updated CLI datasource entry.
     */
    CliDataSource update(CliDataSourceUpdateRequest cliDataSourceUpdateRequest);

    /**
     * Deletes a datasource for CLI callers.
     *
     * @param dataSourceId datasource identifier.
     */
    void delete(Long dataSourceId);
}
