package ai.chat2db.community.web.api.model.response.cli;

import java.util.Map;

import ai.chat2db.community.tools.wrapper.IResult;
import lombok.Data;

@Data
public class CliResult<T> {

    private Boolean success;

    private T data;

    private CliErrorResponse error;

    private String requestId;

    public static <T> CliResult<T> ok(T data, String requestId) {
        CliResult<T> result = new CliResult<>();
        result.setSuccess(Boolean.TRUE);
        result.setData(data);
        result.setRequestId(requestId);
        return result;
    }

    public static <T> CliResult<T> fromResult(IResult<?> source, String requestId) {
        return error(source.errorCode(), source.errorMessage(), requestId);
    }

    public static <T> CliResult<T> error(String code, String message, String requestId) {
        return error(code, message, Map.of(), requestId);
    }

    public static <T> CliResult<T> error(String code, String message, Map<String, Object> details, String requestId) {
        CliResult<T> result = new CliResult<>();
        result.setSuccess(Boolean.FALSE);
        result.setError(new CliErrorResponse(code, message, details == null ? Map.of() : details));
        result.setRequestId(requestId);
        return result;
    }
}
