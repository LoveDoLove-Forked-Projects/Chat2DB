package ai.chat2db.community.tools.console;

import lombok.Data;

import java.util.Map;

@Data
public class ConsoleMessage {

    private String uuid;

    private String message;

    private String actionType;

    private String token;

    private String requestUrl;

    private String method;

    private Map<String, Object> headers;

    public static class ActionType {
        public static final String EXECUTE = "execute";
        public static final String LOGIN = "login";
        public static final String PING = "ping";
        public static final String OPEN_SESSION = "open_session";
        public static final String ERROR = "error";
        public static final String MESSAGE = "message";

        private ActionType() {
        }
    }
}
