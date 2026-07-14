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


@JcefAction(value = "reveal-in-explorer", method = "client-command")
public class RevealInExplorerHandler implements IJcefActionHandler{
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        JSONObject message = JSON.parseObject(consoleMessage.getMessage());
        String filePath = message.getString("path");
        OSOperateUtil.openFileManager(filePath);
        ResponseBuilder.buildSuccessJcef(Map.of("success", true), callback);
    }
}
