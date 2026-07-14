package ai.chat2db.community.domain.api.service.db;

import ai.chat2db.community.domain.api.model.request.db.DbDlCountRequest;
import ai.chat2db.community.domain.api.model.request.db.DbCopyInValuesRequest;
import ai.chat2db.community.domain.api.model.request.db.DbDlExecuteRequest;
import ai.chat2db.community.domain.api.model.request.sql.DbSqlValidateRequest;
import ai.chat2db.community.domain.api.model.request.db.DbSelectResultUpdateRequest;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;

import java.util.List;

/**
 * Executes SQL statements and builds helper SQL for editable query results.
 */
public interface IDbDlTemplateService {

    /**
     * Executes SQL and returns all produced execution results.
     *
     * @param dbDlExecuteRequest SQL execution parameters.
     * @return execution results.
     */
    List<ExecuteResponse> execute(DbDlExecuteRequest dbDlExecuteRequest);

    /**
     * Executes DDL SQL and returns the primary execution result used by table-edit flows.
     *
     * @param dbDlExecuteRequest SQL execution parameters.
     * @return execution result.
     */
    ExecuteResponse executeDdl(DbDlExecuteRequest dbDlExecuteRequest);

    /**
     * Executes update SQL and returns the affected execution result.
     *
     * @param dbDlExecuteRequest SQL execution parameters.
     * @return execution result.
     */
    ExecuteResponse executeUpdate(DbDlExecuteRequest dbDlExecuteRequest);

    /**
     * Executes a table select request and returns query results.
     *
     * @param dbDlExecuteRequest SQL execution parameters.
     * @return execution results.
     */
    List<ExecuteResponse> executeSelectTable(DbDlExecuteRequest dbDlExecuteRequest);

    /**
     * Executes a count query and returns the row count.
     *
     * @param dbDlCountRequest count-query parameters.
     * @return row count.
     */
    Long count(DbDlCountRequest dbDlCountRequest);

    /**
     * Builds SQL for updating editable select-result rows.
     *
     * @param dbSelectResultUpdateRequest editable select-result update parameters.
     * @return generated SQL text.
     */
    String updateSelectResult(DbSelectResultUpdateRequest dbSelectResultUpdateRequest);

    /**
     * Builds SQL for copying editable select-result rows.
     *
     * @param dbSelectResultUpdateRequest editable select-result update parameters.
     * @return generated SQL text.
     */
    String copySelectResult(DbSelectResultUpdateRequest dbSelectResultUpdateRequest);

    /**
     * Builds a SQL IN-value list from selected result cells.
     *
     * @param dbCopyInValuesRequest selected cell values used to build SQL IN values.
     * @return generated SQL text.
     */
    String copyInValues(DbCopyInValuesRequest dbCopyInValuesRequest);

    /**
     * Validates SQL and returns the validation execution result.
     *
     * @param dbSqlValidateRequest SQL validation parameters.
     * @return validation execution result.
     */
    ExecuteResponse validate(DbSqlValidateRequest dbSqlValidateRequest);

}
