package ai.chat2db.community.web.api.model.response.cli;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CliErrorResponse {

    private String code;

    private String message;

    private Map<String, Object> details;
}
