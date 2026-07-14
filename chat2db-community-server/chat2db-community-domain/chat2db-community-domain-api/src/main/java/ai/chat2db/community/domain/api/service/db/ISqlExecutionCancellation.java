package ai.chat2db.community.domain.api.service.db;


@FunctionalInterface
public interface ISqlExecutionCancellation {

    /**
     * Checks whether the current SQL execution should stop.
     *
     * @return true when cancellation has been requested; false otherwise.
     */
    boolean isCanceled();
}
