package ai.chat2db.community.tools.console;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsoleResult {

    private String uuid;

    private Map<String, Object> message;

    private String actionType;

    private String requestUrl;

    private String method;

    private String param;
}
