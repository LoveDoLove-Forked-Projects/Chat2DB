package ai.chat2db.community.domain.api.model.request.operation;

import java.util.List;

import ai.chat2db.community.domain.api.model.result.ExecuteResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OpsSqlOperationLogListResultRequest {

    private String fallbackSql;

    @NotNull
    private Boolean success;

    private String errorMessage;

    private List<ExecuteResponse> results;

    @NotBlank
    private String source;

    public static OpsSqlOperationLogListResultRequest of(String fallbackSql, Boolean success, String errorMessage,
            List<ExecuteResponse> results, String source) {
        OpsSqlOperationLogListResultRequest request = new OpsSqlOperationLogListResultRequest();
        request.setFallbackSql(fallbackSql);
        request.setSuccess(success);
        request.setErrorMessage(errorMessage);
        request.setResults(results);
        request.setSource(source);
        return request;
    }
}
