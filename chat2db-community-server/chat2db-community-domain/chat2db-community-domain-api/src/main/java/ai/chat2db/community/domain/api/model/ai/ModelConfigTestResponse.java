package ai.chat2db.community.domain.api.model.ai;

import lombok.Data;

@Data
public class ModelConfigTestResponse {

    private Boolean success;

    private String message;

    private Integer statusCode;

    private String endpoint;

    public static ModelConfigTestResponse success(String endpoint) {
        ModelConfigTestResponse result = new ModelConfigTestResponse();
        result.setSuccess(Boolean.TRUE);
        result.setMessage("Connection test passed");
        result.setEndpoint(endpoint);
        return result;
    }

    public static ModelConfigTestResponse failure(String endpoint, Integer statusCode, String message) {
        ModelConfigTestResponse result = new ModelConfigTestResponse();
        result.setSuccess(Boolean.FALSE);
        result.setEndpoint(endpoint);
        result.setStatusCode(statusCode);
        result.setMessage(message);
        return result;
    }
}
