package ai.chat2db.spi;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.metadata.RoutineOperation;
import ai.chat2db.community.domain.api.model.sql.SqlPreview;

import java.sql.Connection;

/**
 * Builds previews and executes dialect-specific routine operations.
 */
public interface IRoutineManager {

    /**
     * Builds a preview for invoking a routine.
     *
     * @param connection active database connection.
     * @param operation routine invocation metadata.
     * @return SQL preview for invoking the routine.
     */
    SqlPreview previewInvocation(Connection connection, RoutineOperation operation);

    /**
     * Builds a preview for migrating or updating a routine definition.
     *
     * @param connection active database connection.
     * @param operation routine migration metadata.
     * @return SQL preview for the migration.
     */
    SqlPreview previewMigration(Connection connection, RoutineOperation operation);

    /**
     * Executes a routine migration operation.
     *
     * @param connection active database connection.
     * @param operation routine migration metadata.
     * @return execution result produced by the migration SQL.
     */
    ExecuteResponse executeMigration(Connection connection, RoutineOperation operation);
}
