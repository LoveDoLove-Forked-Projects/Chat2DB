package ai.chat2db.community.domain.api.service.cli;

import java.util.List;

import ai.chat2db.community.domain.api.model.cli.CliColumn;
import ai.chat2db.community.domain.api.model.cli.CliDatabase;
import ai.chat2db.community.domain.api.model.cli.CliIndex;
import ai.chat2db.community.domain.api.model.cli.CliPage;
import ai.chat2db.community.domain.api.model.cli.CliSchema;
import ai.chat2db.community.domain.api.model.cli.CliTable;
import ai.chat2db.community.domain.api.model.request.cli.CliConnectionResolveRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliTableDetailRequest;
import ai.chat2db.community.domain.api.model.request.cli.CliTablesListRequest;

/**
 * Exposes metadata lookup operations for CLI callers.
 */
public interface ICliMetadataService {

    /**
     * Lists databases for a CLI connection.
     *
     * @param cliConnectionResolveRequest CLI connection parameters.
     * @return database metadata visible to the CLI connection.
     */
    List<CliDatabase> listDatabases(CliConnectionResolveRequest cliConnectionResolveRequest);

    /**
     * Lists schemas for a CLI connection.
     *
     * @param cliConnectionResolveRequest CLI connection parameters.
     * @return schema metadata visible to the CLI connection.
     */
    List<CliSchema> listSchemas(CliConnectionResolveRequest cliConnectionResolveRequest);

    /**
     * Lists tables for a CLI connection with pagination.
     *
     * @param cliTablesListRequest CLI table listing parameters.
     * @return paged CLI table metadata.
     */
    CliPage<CliTable> listTables(CliTablesListRequest cliTablesListRequest);

    /**
     * Returns detailed table metadata for CLI callers.
     *
     * @param cliTableDetailRequest CLI table detail parameters.
     * @return CLI table metadata, or null when no matching table exists.
     */
    CliTable tableDetail(CliTableDetailRequest cliTableDetailRequest);

    /**
     * Lists columns for a CLI table.
     *
     * @param cliTableDetailRequest CLI table detail parameters.
     * @return CLI column metadata.
     */
    List<CliColumn> columns(CliTableDetailRequest cliTableDetailRequest);

    /**
     * Lists indexes for a CLI table.
     *
     * @param cliTableDetailRequest CLI table detail parameters.
     * @return CLI index metadata.
     */
    List<CliIndex> indexes(CliTableDetailRequest cliTableDetailRequest);
}
