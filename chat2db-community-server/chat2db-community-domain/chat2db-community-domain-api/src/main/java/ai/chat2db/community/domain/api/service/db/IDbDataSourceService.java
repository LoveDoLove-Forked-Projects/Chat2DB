package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.datasource.SSHInfo;
import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.request.datasource.DbDataSourcePreConnectRequest;

import java.util.List;

/**
 * Manages datasource connection lifecycle, driver defaults, and SSH connectivity checks.
 */
public interface IDbDataSourceService {

    /**
     * Tests datasource connectivity before a datasource is persisted or opened.
     *
     * @param dbDataSourcePreConnectRequest datasource pre-connection parameters.
     */
    void preConnect(DbDataSourcePreConnectRequest dbDataSourcePreConnectRequest);

    /**
     * Opens a datasource connection and returns available databases.
     *
     * @param id datasource identifier.
     * @return database metadata.
     */
    List<Database> connect(Long id);

    /**
     * Closes an opened datasource connection.
     *
     * @param id datasource identifier.
     */
    void close(Long id);

    /**
     * Returns the default JDBC driver configuration for a database type.
     *
     * @param dbType database type code used to select dialect-specific behavior.
     * @return default driver configuration for the database type.
     */
    DriverConfig defaultDriverConfig(String dbType);

    /**
     * Removes cached connection resources for a datasource.
     *
     * @param id datasource identifier.
     */
    void removeConnection(Long id);

    /**
     * Tests whether an SSH tunnel configuration can be opened.
     *
     * @param ssh SSH connection configuration to test.
     */
    void testSshConnection(SSHInfo ssh);

    /**
     * Closes all runtime connection resources owned by the datasource layer.
     */
    void closeRuntime();

}
