package ai.chat2db.community.jcef.handler.biz.update;


import ai.chat2db.community.jcef.update.Updater;
import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.handler.biz.IJcefActionHandler;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import org.cef.callback.CefQueryCallback;

import java.nio.file.Path;
import java.util.Map;


@JcefAction(value = "trigger-download", method = "client-command")
public class TriggerDownloadHandler implements IJcefActionHandler {
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        Updater updater = Updater.getInstance();
        Map<String, Path> fileMap = updater.triggerDownload(wsResult);
        ResponseBuilder.buildSuccessJcef(Map.of("data", fileMap != null), callback);
    }
}
