package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.metadata.RoutineOperation;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.sql.SqlPreview;

/**
 * Previews and executes routine invocation and migration operations.
 */
public interface IDbRoutineOperationService {

    /**
     * Previews SQL for invoking a routine operation.
     *
     * @param operation routine operation metadata.
     * @return SQL preview for the invocation.
     */
    SqlPreview previewInvocation(RoutineOperation operation);

    /**
     * Previews SQL for migrating a routine operation.
     *
     * @param operation routine operation metadata.
     * @return SQL preview for the migration.
     */
    SqlPreview previewMigration(RoutineOperation operation);

    /**
     * Executes a routine migration operation.
     *
     * @param operation routine operation metadata.
     * @return execution result for the migration.
     */
    ExecuteResponse executeMigration(RoutineOperation operation);
}
