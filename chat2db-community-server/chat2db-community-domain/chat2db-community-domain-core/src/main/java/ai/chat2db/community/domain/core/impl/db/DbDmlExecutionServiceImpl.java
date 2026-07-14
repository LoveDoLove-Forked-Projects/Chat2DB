package ai.chat2db.community.domain.core.impl.db;

import ai.chat2db.community.domain.api.model.request.db.DbDlExecuteRequest;
import ai.chat2db.community.domain.api.model.request.db.DbDmlExecutionRequest;
import ai.chat2db.community.domain.api.model.request.db.DbLargeValueTokensAttachRequest;
import ai.chat2db.community.domain.api.model.request.db.DbSelectResultUpdateRequest;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.result.ResultOperation;
import ai.chat2db.community.domain.api.service.db.IDbDlTemplateService;
import ai.chat2db.community.domain.api.service.db.IDbDmlExecutionService;
import ai.chat2db.community.domain.api.service.db.IDbLargeValueTokenService;
import ai.chat2db.community.domain.api.service.ops.IOpsSqlOperationLogService;
import ai.chat2db.community.tools.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbDmlExecutionServiceImpl implements IDbDmlExecutionService {

    private static final String LARGE_VALUE_PREVIEW_PREFIX = "CHAT2DB_LARGE_VALUE_PREVIEW:";

    private final IDbDlTemplateService dlTemplateService;

    private final IDbLargeValueTokenService largeValueTokenService;

    private final IOpsSqlOperationLogService sqlOperationLogRecorder;

    public DbDmlExecutionServiceImpl(IDbDlTemplateService dlTemplateService,
            IDbLargeValueTokenService largeValueTokenService,
            IOpsSqlOperationLogService sqlOperationLogRecorder) {
        this.dlTemplateService = dlTemplateService;
        this.largeValueTokenService = largeValueTokenService;
        this.sqlOperationLogRecorder = sqlOperationLogRecorder;
    }

    @Override
    public List<ExecuteResponse> execute(DbDmlExecutionRequest request) {
        return executeAndRecord(request, dlTemplateService::execute);
    }

    @Override
    public List<ExecuteResponse> executeTable(DbDmlExecutionRequest request) {
        return executeAndRecord(request, dlTemplateService::executeSelectTable);
    }

    @Override
    public ExecuteResponse executeUpdate(DbDmlExecutionRequest request) {
        boolean operationLogged = false;
        DbDlExecuteRequest executeRequest = requireExecuteRequest(request);
        try {
            ExecuteResponse result = dlTemplateService.executeUpdate(executeRequest);
            if (result != null) {
                sqlOperationLogRecorder.recordResultAsync(result, request.getSource());
                operationLogged = true;
            }
            return result;
        } catch (RuntimeException e) {
            recordFailureIfNeeded(executeRequest, request.getSource(), operationLogged, e);
            throw e;
        }
    }

    @Override
    public ExecuteResponse executeDdl(DbDmlExecutionRequest request) {
        DbDlExecuteRequest executeRequest = requireExecuteRequest(request);
        try {
            ExecuteResponse result = dlTemplateService.executeDdl(executeRequest);
            if (result != null) {
                sqlOperationLogRecorder.recordResultAsync(result, request.getSource());
            }
            return result;
        } catch (RuntimeException e) {
            sqlOperationLogRecorder.recordFailureAsync(executeRequest.getSql(), request.getSource(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void rejectPartialLargeValueOperations(DbSelectResultUpdateRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getOperations())) {
            return;
        }
        for (ResultOperation operation : request.getOperations()) {
            if (containsLargeValuePlaceholder(operation.getDataList())
                    || containsLargeValuePlaceholder(operation.getOldDataList())) {
                throw new BusinessException("largeCellValue.partialPreviewEditRejected");
            }
        }
    }

    private List<ExecuteResponse> executeAndRecord(DbDmlExecutionRequest request,
            ExecuteFunction executeFunction) {
        boolean operationLogged = false;
        DbDlExecuteRequest executeRequest = requireExecuteRequest(request);
        try {
            List<ExecuteResponse> results = executeFunction.execute(executeRequest);
            sqlOperationLogRecorder.recordResultsAsync(results, request.getSource());
            operationLogged = true;
            attachLargeValueTokens(executeRequest, results);
            return results;
        } catch (RuntimeException e) {
            recordFailureIfNeeded(executeRequest, request.getSource(), operationLogged, e);
            throw e;
        }
    }

    private DbDlExecuteRequest requireExecuteRequest(DbDmlExecutionRequest request) {
        if (request == null || request.getExecuteRequest() == null) {
            throw new BusinessException("request.required");
        }
        return request.getExecuteRequest();
    }

    private void recordFailureIfNeeded(DbDlExecuteRequest executeRequest, String source, boolean operationLogged,
            RuntimeException e) {
        if (!operationLogged) {
            sqlOperationLogRecorder.recordFailureAsync(executeRequest.getSql(), source, e.getMessage());
        }
    }

    private void attachLargeValueTokens(DbDlExecuteRequest executeRequest, List<ExecuteResponse> results) {
        if (CollectionUtils.isEmpty(results)) {
            return;
        }
        for (ExecuteResponse executeResult : results) {
            largeValueTokenService.attachTokens(largeValueTokensAttachRequest(executeRequest, executeResult));
        }
    }

    private DbLargeValueTokensAttachRequest largeValueTokensAttachRequest(DbDlExecuteRequest executeRequest,
            ExecuteResponse executeResponse) {
        DbLargeValueTokensAttachRequest request = new DbLargeValueTokensAttachRequest();
        request.setDataSourceId(executeRequest.getDataSourceId());
        request.setDatabaseName(executeRequest.getDatabaseName());
        request.setSchemaName(executeRequest.getSchemaName());
        request.setTableName(executeResponse.getTableName());
        request.setHeaders(executeResponse.getHeaderList());
        request.setDataList(executeResponse.getDataList());
        request.setCanEdit(executeResponse.isCanEdit());
        return request;
    }

    private boolean containsLargeValuePlaceholder(List<String> row) {
        if (CollectionUtils.isEmpty(row)) {
            return false;
        }
        return row.stream().anyMatch(value -> StringUtils.startsWith(value, LARGE_VALUE_PREVIEW_PREFIX));
    }

    @FunctionalInterface
    private interface ExecuteFunction {

        List<ExecuteResponse> execute(DbDlExecuteRequest request);
    }
}
