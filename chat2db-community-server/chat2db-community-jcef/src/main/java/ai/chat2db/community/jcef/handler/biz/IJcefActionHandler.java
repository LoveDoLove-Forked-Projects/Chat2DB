package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import org.cef.callback.CefQueryCallback;

@FunctionalInterface
public interface IJcefActionHandler {


    void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception;
}
