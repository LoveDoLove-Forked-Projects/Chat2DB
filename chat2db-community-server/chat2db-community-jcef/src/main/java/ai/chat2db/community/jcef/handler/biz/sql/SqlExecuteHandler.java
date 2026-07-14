package ai.chat2db.community.jcef.handler.biz.sql;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.handler.biz.IJcefActionHandler;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import ai.chat2db.community.tools.console.bridge.JcefServerBridgeRegistry;
import ai.chat2db.community.tools.console.bridge.SqlExecuteResponse;
import org.cef.callback.CefQueryCallback;

import java.util.Map;

@JcefAction(value = "sql-execute", method = "client-command")
public class SqlExecuteHandler implements IJcefActionHandler {

    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) {
        SqlExecuteResponse startResult = JcefServerBridgeRegistry.getBridge().executeSql(consoleMessage);
        ResponseBuilder.buildSuccessJcef(Map.of("data", startResult.getStartResult()), callback);
    }
}
