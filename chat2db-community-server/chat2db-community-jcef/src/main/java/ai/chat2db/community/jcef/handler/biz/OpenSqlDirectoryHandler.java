package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.cef.callback.CefQueryCallback;

import java.util.Map;

@JcefAction(value = "open-sql-directory", method = "client-command")
public class OpenSqlDirectoryHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) {
        try {
            JSONObject jsonObject = JSON.parseObject(consoleMessage.getMessage());
            String path = jsonObject.getString("path");
            if (StringUtils.isBlank(path)) {
                ResponseBuilder.buildSuccessJcef(Map.of("data", null), callback);
                return;
            }

            ResponseBuilder.buildSuccessJcef(Map.of("data", SqlDirectoryTreeStore.createRoot(path)), callback);
        } catch (Exception exception) {
            callback.failure(500, exception.getMessage());
        }
    }
}
