package ai.chat2db.community.domain.api.service.task;

import java.util.List;

public interface ITaskImportSqlExecutor {

    /**
     * Executes a batch of SQL statements during task import.
     *
     * @param batch batch index.
     * @param sqls SQL statements in execution order.
     * @return execution summary or error message.
     */
    String executeBatch(int batch, List<String> sqls);

    /**
     * Executes one SQL statement during task import.
     *
     * @param batch batch index.
     * @param sql SQL statement to execute.
     * @return execution summary or error message.
     */
    String executeSql(int batch, String sql);
}
