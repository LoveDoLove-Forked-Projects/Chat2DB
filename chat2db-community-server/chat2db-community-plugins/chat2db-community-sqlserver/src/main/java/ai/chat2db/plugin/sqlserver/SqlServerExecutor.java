package ai.chat2db.plugin.sqlserver;

import ai.chat2db.community.domain.api.model.sql.SqlExecuteRequest;
import ai.chat2db.community.domain.api.model.result.ExecutionContext;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.sql.SimpleSqlStatement;
import ai.chat2db.spi.model.ExecutionTiming;
import ai.chat2db.spi.model.JdbcExecutionContext;
import ai.chat2db.spi.model.request.SqlStatementExecuteRequest;
import ai.chat2db.spi.DefaultSQLExecutor;
import cn.hutool.core.date.TimeInterval;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static ai.chat2db.plugin.sqlserver.constant.SqlServerExecutorConstants.*;
public class SqlServerExecutor extends DefaultSQLExecutor {



    @Override
    public List<ExecuteResponse> execute(SqlExecuteRequest command) {
        prepareCommandScript(command);
        return super.execute(command);
    }

    void prepareCommandScript(SqlExecuteRequest command) {
        String sql = command.getScript();
        if (!command.isExplain() && splitByGO(sql).size() <= 1) {
            command.setScript(removeSpecialGO(sql));
        }
    }


    private String removeSpecialGO(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        return GO_DELIMITER_PATTERN.matcher(sql).replaceAll("");
    }

    List<String> splitByGO(String sql) {
        List<String> sqlList = new ArrayList<>();
        if (StringUtils.isBlank(sql)) {
            return sqlList;
        }
        int start = 0;
        var matcher = GO_DELIMITER_PATTERN.matcher(sql);
        while (matcher.find()) {
            String item = sql.substring(start, matcher.start()).trim();
            if (StringUtils.isNotBlank(item)) {
                sqlList.add(item);
            }
            start = matcher.end();
        }
        String item = sql.substring(start).trim();
        if (StringUtils.isNotBlank(item)) {
            sqlList.add(item);
        }
        return sqlList;
    }


    @Override
    public ExecuteResponse executeUpdate(String sql, Connection connection, int n) throws SQLException {
        sql = removeSpecialGO(sql);
        return super.executeUpdate(sql, connection, n);
    }


    public ExecuteResponse execute(SqlStatementExecuteRequest request)
            throws SQLException {
        String sql = request.getSql();
        List<String> sqlList = splitByGO(sql);
        if (sqlList.size() > 1) {
            return executeSqlServerBatch(sql, sqlList, request.getConnection(), request.isLimitRowSize(),
                    request.getOffset(), request.getCount());
        }
        return super.execute(SqlStatementExecuteRequest.builder()
                .sql(removeSpecialGO(sql))
                .connection(request.getConnection())
                .limitRowSize(request.isLimitRowSize())
                .offset(request.getOffset())
                .count(request.getCount())
                .build());
    }

    @Override
    protected List<ExecuteResponse> executeMulti(SimpleSqlStatement simpleSqlStatement, Connection connection,
                                               boolean limitRowSize, Integer offset, Integer count, Integer resultSetId)
            throws SQLException {
        List<String> sqlList = splitByGO(simpleSqlStatement.getSql());
        if (sqlList.size() <= 1) {
            simpleSqlStatement.setSql(removeSpecialGO(simpleSqlStatement.getSql()));
            return super.executeMulti(simpleSqlStatement, connection, limitRowSize, offset, count, resultSetId);
        }
        return executeSqlServerBatch(simpleSqlStatement.getSql(), sqlList, connection, limitRowSize, offset, count,
                resultSetId);
    }

    private ExecuteResponse executeSqlServerBatch(String originalSql, List<String> sqlList, Connection connection,
                                                boolean limitRowSize, Integer offset, Integer count)
            throws SQLException {
        List<ExecuteResponse> executeResults = executeSqlServerBatch(originalSql, sqlList, connection, limitRowSize,
                offset, count, null);
        if (executeResults.isEmpty()) {
            return ExecuteResponse.builder().sql(originalSql).success(Boolean.TRUE).build();
        }
        return executeResults.get(executeResults.size() - 1);
    }

    private List<ExecuteResponse> executeSqlServerBatch(String originalSql, List<String> sqlList, Connection connection,
                                                      boolean limitRowSize, Integer offset, Integer count,
                                                      Integer resultSetId)
            throws SQLException {
        List<ExecuteResponse> executeResults = new ArrayList<>();
        ExecuteResponse executeResult = ExecuteResponse.builder().sql(originalSql).success(Boolean.TRUE).build();
        TimeInterval timeInterval = new TimeInterval();
        int resultCount = 0;
        for (String sql : sqlList) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ExecutionContext executionContext = JdbcExecutionContext.capture(connection);
                long startedAtEpochMs = System.currentTimeMillis();
                long executeStartedNanos = System.nanoTime();
                boolean query = stmt.execute();
                long executeDurationNanos = ExecutionTiming.elapsedNanos(executeStartedNanos);
                while (true) {
                    executeResult = ExecuteResponse.builder().sql(originalSql).success(Boolean.TRUE).build();
                    long fetchDurationNanos = 0L;
                    if (query) {
                        resultCount++;
                        if (resultSetId == null || resultCount == resultSetId) {
                            long fetchStartedNanos = System.nanoTime();
                            executeResult = generateQueryExecuteResponse(stmt, limitRowSize, offset, count);
                            fetchDurationNanos = ExecutionTiming.elapsedNanos(fetchStartedNanos);
                            executeResult.setResultSetId(resultCount);
                        }
                    } else {
                        int updateCount = stmt.getUpdateCount();
                        if (updateCount == -1) {
                            break;
                        }
                        executeResult.setUpdateCount(updateCount);
                    }
                    executeResult.setSql(originalSql);
                    executeResult.setDuration(timeInterval.interval());
                    executeResult.setStatementSequence(1);
                    executeResult.setExecutionContext(executionContext);
                    executeResult.setExecutionMetrics(ExecutionTiming.complete(
                            ExecutionTiming.started(startedAtEpochMs), executeDurationNanos, fetchDurationNanos,
                            CollectionUtils.size(executeResult.getDataList())));
                    executeResults.add(executeResult);
                    long nextStartedAtEpochMs = System.currentTimeMillis();
                    long nextExecuteStartedNanos = System.nanoTime();
                    query = stmt.getMoreResults();
                    long nextExecuteDurationNanos = ExecutionTiming.elapsedNanos(nextExecuteStartedNanos);
                    if (!query && stmt.getUpdateCount() == -1) {
                        break;
                    }
                    startedAtEpochMs = nextStartedAtEpochMs;
                    executeDurationNanos = nextExecuteDurationNanos;
                }
            }
        }
        return executeResults;
    }
}
