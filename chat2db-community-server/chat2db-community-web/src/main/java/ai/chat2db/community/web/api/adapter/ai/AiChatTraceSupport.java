package ai.chat2db.community.web.api.adapter.ai;

import org.springframework.ai.chat.model.ToolContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class AiChatTraceSupport {

    public static final String TRACE_EMITTER_KEY = "_chat2db_trace_emitter";

    public static final String TYPE_REASONING = "reasoning";

    public static final String TYPE_TOOL_CALL = "tool_call";

    public static final String TYPE_TOOL_RESULT = "tool_result";

    public static final String TYPE_ANSWER = "answer";

    public static final String TYPE_DONE = "done";

    public static final String TYPE_ERROR = "error";

    private AiChatTraceSupport() {
    }

    public static Map<String, Object> payload(String type) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", type);
        payload.put("messageType", type);
        return payload;
    }

    @SuppressWarnings("unchecked")
    public static void emit(ToolContext toolContext, Map<String, Object> payload) {
        if (toolContext == null || toolContext.getContext() == null || payload == null) {
            return;
        }
        Object emitter = toolContext.getContext().get(TRACE_EMITTER_KEY);
        if (emitter instanceof Consumer<?> consumer) {
            ((Consumer<Map<String, Object>>) consumer).accept(payload);
        }
    }
}
