package ai.chat2db.community.domain.api.service.ops;

import ai.chat2db.community.domain.api.model.operation.SqlOperationLogRecord;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.request.operation.OpsSqlOperationLogListResultRequest;

import java.util.List;

public interface IOpsSqlOperationLogService {

    /**
     * Records multiple SQL execution results asynchronously.
     *
     * @param results execution responses to record.
     * @param source source that produced the log records.
     */
    void recordResultsAsync(List<ExecuteResponse> results, String source);

    /**
     * Records SQL execution results from a list-result wrapper asynchronously.
     *
     * @param opsSqlOperationLogListResultRequest execution list-result record parameters.
     */
    void recordListResultAsync(OpsSqlOperationLogListResultRequest opsSqlOperationLogListResultRequest);

    /**
     * Records one SQL execution result asynchronously.
     *
     * @param result execution response to record.
     * @param source source that produced the log record.
     */
    void recordResultAsync(ExecuteResponse result, String source);

    /**
     * Records a SQL execution failure asynchronously.
     *
     * @param sql SQL text that failed.
     * @param source source that produced the log record.
     * @param errorMessage failure message to persist.
     */
    void recordFailureAsync(String sql, String source, String errorMessage);

    /**
     * Records SQL operation log entries asynchronously.
     *
     * @param record SQL operation log record.
     */
    void recordAsync(SqlOperationLogRecord record);

    /**
     * Records SQL operation log entries asynchronously.
     *
     * @param records SQL operation log records.
     */
    void recordAsync(List<SqlOperationLogRecord> records);
}
