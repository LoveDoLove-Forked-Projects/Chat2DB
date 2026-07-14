package ai.chat2db.community.jcef.handler.biz;


import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;

import java.util.Map;


@JcefAction(value = "web-frame-set-zoom", method = "client-command")
public class WebFrameSetZoomHandler implements IJcefActionHandler{
    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        JSONObject jsonObject = JSON.parseObject(consoleMessage.getMessage());
        String action = jsonObject.getString("action");
        CefBrowser browser = JcefContext.getInstance().getBrowser_();
        switch (action) {
            case "zoomIn":
                browser.setZoomLevel(browser.getZoomLevel() + 0.25);
                break;
            case "zoomOut":
                browser.setZoomLevel(browser.getZoomLevel() - 0.25);
                break;
            case "zoomReset":
                browser.setZoomLevel(0.0);
                break;
        }
        ResponseBuilder.buildSuccessJcef(Map.of("data", true), callback);
    }
}
