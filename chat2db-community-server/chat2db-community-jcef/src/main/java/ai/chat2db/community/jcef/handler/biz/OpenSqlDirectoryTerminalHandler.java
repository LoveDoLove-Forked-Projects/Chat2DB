package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.utils.OSOperateUtil;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cef.callback.CefQueryCallback;

import java.util.Map;

@JcefAction(value = "open-sql-directory-terminal", method = "client-command")
public class OpenSqlDirectoryTerminalHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) {
        try {
            JSONObject jsonObject = JSON.parseObject(consoleMessage.getMessage());
            String rootToken = jsonObject.getString("rootToken");
            String relativePath = jsonObject.getString("relativePath");
            String directoryPath = SqlDirectoryTreeStore.getDirectoryPath(rootToken, relativePath);

            OSOperateUtil.openTerminal(directoryPath);
            ResponseBuilder.buildSuccessJcef(Map.of("data", true), callback);
        } catch (Exception exception) {
            callback.failure(500, exception.getMessage());
        }
    }
}
