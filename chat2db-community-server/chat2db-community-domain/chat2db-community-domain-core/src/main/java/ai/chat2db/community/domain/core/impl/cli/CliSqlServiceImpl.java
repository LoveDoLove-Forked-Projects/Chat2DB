package ai.chat2db.community.domain.core.impl.cli;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ai.chat2db.community.tools.exception.cli.CliDomainException;
import ai.chat2db.community.domain.api.model.cli.CliSqlQueryResponse;
import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import ai.chat2db.community.domain.api.model.request.cli.CliSqlQueryRequest;
import ai.chat2db.community.domain.api.model.request.db.DbDlExecuteRequest;
import ai.chat2db.community.domain.api.service.db.IDbDlTemplateService;
import ai.chat2db.community.domain.api.service.cli.ICliSqlService;
import ai.chat2db.community.domain.core.converter.CliSqlDomainConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CliSqlServiceImpl implements ICliSqlService {

    private static final int SQL_PREVIEW_LIMIT = 160;

    private final IDbDlTemplateService dlTemplateService;
    private final CliSqlDomainConverter cliSqlConverter;

    public CliSqlServiceImpl(IDbDlTemplateService dlTemplateService, CliSqlDomainConverter cliSqlConverter) {
        this.dlTemplateService = dlTemplateService;
        this.cliSqlConverter = cliSqlConverter;
    }

    @Override
    public CliSqlQueryResponse query(CliSqlQueryRequest request) {
        DbDlExecuteRequest param = new DbDlExecuteRequest();
        param.setDataSourceId(request.getDataSourceId());
        param.setDatabaseName(request.getDatabaseName());
        param.setSchemaName(request.getSchemaName());
        param.setSql(request.getSql());
        param.setPageNo(request.safePageNo());
        param.setPageSize(request.safePageSize());
        param.setResultSetId(request.getResultSetId());
        param.setSingle(true);
        List<ExecuteResponse> result = dlTemplateService.execute(param);
        ExecuteResponse executeResult = selectResult(result, request.getResultSetId());
        if (executeResult == null) {
            return cliSqlConverter.emptyQueryResponse(request);
        }
        if (!Boolean.TRUE.equals(executeResult.getSuccess())) {
            throw new CliDomainException("sql_query_failed",
                    defaultErrorMessage(executeResult.getMessage(), "SQL query failed."),
                    executeResultDetails(executeResult));
        }
        return cliSqlConverter.executeResult2response(executeResult, request);
    }

    private ExecuteResponse selectResult(List<ExecuteResponse> results, Integer resultSetId) {
        if (results == null || results.isEmpty()) {
            return null;
        }
        if (resultSetId != null) {
            return results.stream()
                    .filter(Objects::nonNull)
                    .filter(result -> resultSetId.equals(result.getResultSetId()))
                    .findFirst()
                    .orElseThrow(() -> new CliDomainException("sql_result_set_not_found",
                            "SQL result set not found: " + resultSetId,
                            Map.of("resultSetId", resultSetId, "availableResultSetIds", availableResultSetIds(results))));
        }
        return results.get(0);
    }

    private String defaultErrorMessage(String message, String fallback) {
        return StringUtils.defaultIfBlank(message, fallback);
    }

    private Map<String, Object> executeResultDetails(ExecuteResponse result) {
        if (result == null) {
            return Map.of();
        }
        return Map.of(
                "sqlPreview", sqlPreview(result.getSql()),
                "sqlLength", result.getSql() == null ? 0 : result.getSql().length(),
                "description", StringUtils.defaultString(result.getDescription()),
                "sqlType", StringUtils.defaultString(result.getSqlType()),
                "resultSetId", result.getResultSetId() == null ? 0 : result.getResultSetId()
        );
    }

    private List<Integer> availableResultSetIds(List<ExecuteResponse> results) {
        if (results == null) {
            return Collections.emptyList();
        }
        return results.stream()
                .filter(Objects::nonNull)
                .map(ExecuteResponse::getResultSetId)
                .filter(Objects::nonNull)
                .toList();
    }

    private String defaultErrorCode(String code, String fallback) {
        return StringUtils.defaultIfBlank(code, fallback);
    }

    private Map<String, Object> errorDetails(String detail) {
        if (StringUtils.isBlank(detail)) {
            return Map.of();
        }
        return Map.of("errorDetail", detail);
    }

    private String sqlPreview(String sql) {
        String value = StringUtils.defaultString(sql);
        if (value.length() <= SQL_PREVIEW_LIMIT) {
            return value;
        }
        return value.substring(0, SQL_PREVIEW_LIMIT) + "...";
    }
}
