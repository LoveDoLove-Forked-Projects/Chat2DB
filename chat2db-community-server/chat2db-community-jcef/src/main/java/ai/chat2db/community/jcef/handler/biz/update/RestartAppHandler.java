package ai.chat2db.community.jcef.handler.biz.update;


import ai.chat2db.community.jcef.update.Updater;
import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.handler.biz.IJcefActionHandler;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import org.cef.callback.CefQueryCallback;

import java.util.Map;


@JcefAction(value = "restart-app", method = "client-command")
public class RestartAppHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        Updater updater = Updater.getInstance();
        updater.restartApp();
        ResponseBuilder.buildSuccessJcef(Map.of("data", true), callback);
    }
}
