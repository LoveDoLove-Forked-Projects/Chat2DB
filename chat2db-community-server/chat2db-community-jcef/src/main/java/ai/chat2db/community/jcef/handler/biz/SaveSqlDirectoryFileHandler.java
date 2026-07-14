package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cef.callback.CefQueryCallback;

import java.util.Map;

@JcefAction(value = "save-sql-directory-file", method = "client-command")
public class SaveSqlDirectoryFileHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) {
        try {
            JSONObject jsonObject = JSON.parseObject(consoleMessage.getMessage());
            String rootToken = jsonObject.getString("rootToken");
            String parentRelativePath = jsonObject.getString("parentRelativePath");
            String name = jsonObject.getString("name");
            String content = jsonObject.getString("content");

            ResponseBuilder.buildSuccessJcef(
                    Map.of("data", SqlDirectoryTreeStore.saveFile(rootToken, parentRelativePath, name, content)),
                    callback
            );
        } catch (Exception exception) {
            callback.failure(500, exception.getMessage());
        }
    }
}
