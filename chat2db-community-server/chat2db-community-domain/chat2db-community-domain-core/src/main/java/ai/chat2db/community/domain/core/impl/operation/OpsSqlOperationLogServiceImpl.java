package ai.chat2db.community.domain.core.impl.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ai.chat2db.community.domain.api.enums.operation.SqlOperationLogStatusEnum;
import ai.chat2db.community.domain.api.model.operation.OperationLog;
import ai.chat2db.community.domain.api.model.operation.SqlOperationLogRecord;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.request.operation.OpsSqlOperationLogListResultRequest;
import ai.chat2db.community.domain.api.service.db.IDbConnectionContextService;
import ai.chat2db.community.domain.api.service.ops.IOpsSqlOperationLogService;
import ai.chat2db.community.domain.api.service.storage.IWorkspaceStorageFacade;
import ai.chat2db.community.domain.core.converter.SqlOperationLogConverter;
import ai.chat2db.community.tools.model.Context;
import ai.chat2db.community.tools.util.ContextUtils;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpsSqlOperationLogServiceImpl implements IOpsSqlOperationLogService {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1,
            4,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(512),
            new OperationLogThreadFactory()
    );

    private final IDbConnectionContextService connectionContextService;
    private final IWorkspaceStorageFacade workspaceStorageFacade;
    private final SqlOperationLogConverter sqlOperationLogConverter;

    public OpsSqlOperationLogServiceImpl(IDbConnectionContextService connectionContextService,
            IWorkspaceStorageFacade workspaceStorageFacade, SqlOperationLogConverter sqlOperationLogConverter) {
        this.connectionContextService = connectionContextService;
        this.workspaceStorageFacade = workspaceStorageFacade;
        this.sqlOperationLogConverter = sqlOperationLogConverter;
    }

    @Override
    public void recordResultsAsync(List<ExecuteResponse> results, String source) {
        if (results == null || results.isEmpty()) {
            return;
        }
        Context context = ContextUtils.queryContext();
        List<SqlOperationLogRecord> records = new ArrayList<>();
        for (ExecuteResponse result : results) {
            SqlOperationLogRecord record = sqlOperationLogConverter.executeResult2record(result, source,
                    connectionContextService.currentProfile(), context);
            if (record != null) {
                records.add(record);
            }
        }
        recordAsync(records);
    }

    @Override
    public void recordListResultAsync(OpsSqlOperationLogListResultRequest sqlOperationLogListResultRequest) {
        if (sqlOperationLogListResultRequest == null) {
            return;
        }
        String fallbackSql = sqlOperationLogListResultRequest.getFallbackSql();
        String source = sqlOperationLogListResultRequest.getSource();
        if (!Boolean.TRUE.equals(sqlOperationLogListResultRequest.getSuccess())) {
            recordFailureAsync(fallbackSql, source,
                    StringUtils.defaultString(sqlOperationLogListResultRequest.getErrorMessage(), "unknown error"));
            return;
        }
        if (sqlOperationLogListResultRequest.getResults() == null
                || sqlOperationLogListResultRequest.getResults().isEmpty()) {
            recordAsync(SqlOperationLogRecord.builder()
                    .sql(fallbackSql)
                    .status(SqlOperationLogStatusEnum.SUCCESS.getCode())
                    .source(source)
                    .connectionProfile(connectionContextService.currentProfile())
                    .context(ContextUtils.queryContext())
                    .build());
            return;
        }
        recordResultsAsync(sqlOperationLogListResultRequest.getResults(), source);
    }

    @Override
    public void recordResultAsync(ExecuteResponse result, String source) {
        if (result == null) {
            return;
        }
        recordAsync(sqlOperationLogConverter.executeResult2record(result, source, connectionContextService.currentProfile(),
                ContextUtils.queryContext()));
    }

    @Override
    public void recordFailureAsync(String sql, String source, String errorMessage) {
        recordAsync(SqlOperationLogRecord.builder()
                .sql(sql)
                .status(SqlOperationLogStatusEnum.FAIL.getCode())
                .errorMessage(errorMessage)
                .source(source)
                .connectionProfile(connectionContextService.currentProfile())
                .context(ContextUtils.queryContext())
                .build());
    }

    @Override
    public void recordAsync(SqlOperationLogRecord record) {
        if (record == null || StringUtils.isBlank(record.getSql())) {
            return;
        }
        if (record.getConnectionProfile() == null) {
            log.warn("skip sql operation log because connect info is missing, payload={}", JSON.toJSONString(record));
            return;
        }
        submit(record);
    }

    @Override
    public void recordAsync(List<SqlOperationLogRecord> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        records.stream()
                .filter(Objects::nonNull)
                .filter(record -> StringUtils.isNotBlank(record.getSql()))
                .forEach(this::recordAsync);
    }

    private void submit(SqlOperationLogRecord record) {
        try {
            executor.execute(() -> write(record));
        } catch (RuntimeException e) { // impl-contract: best-effort - operation logging must not fail the SQL execution.
            log.warn("submit sql operation log failed, payload={}",
                    JSON.toJSONString(sqlOperationLogConverter.sqlRecord2operationLog(record)), e);
        }
    }

    private void write(SqlOperationLogRecord record) {
        Context previousContext = ContextUtils.queryContext();
        try {
            if (record.getContext() != null) {
                ContextUtils.setContext(record.getContext());
            }
            OperationLog operationLog = sqlOperationLogConverter.sqlRecord2operationLog(record);
            workspaceStorageFacade.createOperationLog(operationLog);
        } catch (Exception e) { // impl-contract: best-effort - operation logging must not fail the SQL execution.
            log.warn("record sql operation log failed, payload={}",
                    JSON.toJSONString(sqlOperationLogConverter.sqlRecord2operationLog(record)), e);
        } finally {
            if (previousContext == null) {
                ContextUtils.removeContext();
            } else {
                ContextUtils.setContext(previousContext);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    private static class OperationLogThreadFactory implements ThreadFactory {

        private final AtomicInteger index = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("chat2db-sql-operation-log-" + index.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
