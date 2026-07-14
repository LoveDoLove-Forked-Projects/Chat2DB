package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.config.DBConfig;
import ai.chat2db.community.domain.api.config.DriverConfig;
import ai.chat2db.community.domain.api.model.db.DbDriverConfigView;

import java.io.IOException;
import java.util.List;

public interface IDbJdbcDriverService {

    /**
     * Returns database configuration for a database type.
     *
     * @param dbType database type code used to select driver configuration.
     * @return database configuration.
     */
    DBConfig queryDbConfig(String dbType);

    /**
     * Lists custom JDBC drivers for a database type.
     *
     * @param dbType database type code used to filter drivers.
     * @return custom driver configurations.
     */
    List<DriverConfig> queryCustomDrivers(String dbType);

    /**
     * Lists usable built-in and custom JDBC drivers for a database type.
     *
     * @param dbType database type code used to filter drivers.
     * @return driver configurations whose JAR files are available locally.
     */
    List<DriverConfig> queryAvailableDrivers(String dbType);

    /**
     * Returns database config plus locally available drivers for display.
     *
     * @param dbType database type code used to filter drivers.
     * @return driver display data.
     */
    DbDriverConfigView queryDriverConfigView(String dbType);

    /**
     * Downloads built-in JDBC drivers for a database type.
     *
     * @param dbType database type code used to select built-in drivers.
     * @throws IOException when driver files cannot be downloaded or written.
     */
    void downloadBuiltinDrivers(String dbType) throws IOException;

    /**
     * Downloads built-in JDBC drivers and wraps IO failures in the domain error
     * contract.
     *
     * @param dbType database type code used to select built-in drivers.
     */
    void downloadBuiltinDriversOrThrow(String dbType);

    /**
     * Downloads JDBC drivers required during application startup.
     *
     */
    void downloadStartupDrivers();

    /**
     * Saves a custom JDBC driver configuration.
     *
     * @param driverConfig custom driver configuration.
     */
    void saveCustomDriver(DriverConfig driverConfig);

    /**
     * Saves a custom driver and unloads any stale driver class registration.
     *
     * @param driverConfig driver configuration to save.
     * @param sourceDriverPaths selected source driver files.
     */
    void saveCustomDriver(DriverConfig driverConfig, List<String> sourceDriverPaths);

    /**
     * Copies selected driver JAR files into the managed driver directory.
     *
     * @param driverPaths source driver file paths selected by the user.
     * @return comma-separated driver file names stored in the managed directory.
     */
    String copyDrivers(List<String> driverPaths);

    /**
     * Deletes a custom JDBC driver configuration.
     *
     * @param dbType database type code used to locate the driver.
     * @param jdbcDriver JDBC driver class name.
     * @return deleted driver configuration, or null when no matching driver exists.
     */
    DriverConfig deleteCustomDriver(String dbType, String jdbcDriver);

    /**
     * Deletes the first custom driver referenced by the request and cleans up
     * unreferenced JARs.
     *
     * @param dbType database type code used to locate the driver.
     * @param jdbcDrivers selected JDBC driver identifiers.
     */
    void deleteCustomDriver(String dbType, List<String> jdbcDrivers);

    /**
     * Deletes driver JAR files that are no longer referenced by any custom driver configuration.
     *
     * @param jdbcDriver comma-separated driver JAR file names.
     */
    void deleteUnreferencedDriverJars(String jdbcDriver);

    /**
     * Checks whether a driver JAR is referenced by any driver configuration.
     *
     * @param jarName driver JAR file name.
     * @return true when the JAR is referenced; false otherwise.
     */
    boolean isJarReferenced(String jarName);

    /**
     * Unloads a JDBC driver from the runtime registry.
     *
     * @param jdbcDriver JDBC driver class name.
     */
    void unloadDriver(String jdbcDriver);

    /**
     * Ensures the current runtime supports local JDBC driver management.
     */
    void requireDriverManagementSupported();
}
