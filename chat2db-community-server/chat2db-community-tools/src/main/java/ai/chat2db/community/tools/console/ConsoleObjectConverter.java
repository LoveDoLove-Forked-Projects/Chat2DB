package ai.chat2db.community.tools.console;

import com.alibaba.fastjson2.JSON;

import java.util.Map;

public final class ConsoleObjectConverter {

    private ConsoleObjectConverter() {
    }

    public static Map<String, Object> object2map(Object value) {
        if (value == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), Map.class);
    }
}
