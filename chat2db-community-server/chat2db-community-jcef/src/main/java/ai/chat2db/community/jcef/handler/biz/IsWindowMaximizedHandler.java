package ai.chat2db.community.jcef.handler.biz;


import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.utils.OSOperateUtil;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import org.cef.callback.CefQueryCallback;

import java.util.Map;


@JcefAction(value = "is-window-maximized", method = "client-command")
public class IsWindowMaximizedHandler implements IJcefActionHandler{
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        boolean windowMaximized = OSOperateUtil.isWindowMaximized(JcefContext.getInstance().getFrame_());
        ResponseBuilder.buildSuccessJcef(Map.of("data", windowMaximized), callback);
    }
}
