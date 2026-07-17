package ai.chat2db.spi.model;

import ai.chat2db.community.domain.api.model.result.ExecutionContext;

import java.sql.Connection;
import java.sql.SQLException;

public final class JdbcExecutionContext {

    private JdbcExecutionContext() {
    }

    public static ExecutionContext capture(Connection connection) {
        return ExecutionContext.builder()
                .databaseName(catalog(connection))
                .schemaName(schema(connection))
                .build();
    }

    public static void synchronizeCatalog(Connection connection, String databaseName) {
        if (connection == null || databaseName == null || databaseName.isBlank()) {
            return;
        }
        try {
            connection.setCatalog(databaseName);
        } catch (SQLException | RuntimeException | AbstractMethodError exception) {
            // Context capture is best effort for drivers that do not support catalogs.
        }
    }

    public static void synchronizeSchema(Connection connection, String schemaName) {
        if (connection == null || schemaName == null || schemaName.isBlank()) {
            return;
        }
        try {
            connection.setSchema(schemaName);
        } catch (SQLException | RuntimeException | AbstractMethodError exception) {
            // Context capture is best effort for drivers that do not support schemas.
        }
    }

    private static String catalog(Connection connection) {
        try {
            return connection.getCatalog();
        } catch (SQLException | RuntimeException exception) {
            return null;
        }
    }

    private static String schema(Connection connection) {
        try {
            return connection.getSchema();
        } catch (SQLException | RuntimeException | AbstractMethodError exception) {
            return null;
        }
    }
}
