package ai.chat2db.community.jcef.handler.biz.sql;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.handler.biz.IJcefActionHandler;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import ai.chat2db.community.tools.console.bridge.JcefServerBridgeRegistry;
import org.cef.callback.CefQueryCallback;

import java.util.Map;

@JcefAction(value = "sql-cancel", method = "client-command")
public class SqlCancelHandler implements IJcefActionHandler {

    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) {
        boolean cancelled = JcefServerBridgeRegistry.getBridge().cancelSql(consoleMessage);
        ResponseBuilder.buildSuccessJcef(Map.of("data", cancelled), callback);
    }
}
