package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cef.callback.CefQueryCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Map;


@JcefAction(value = "open-web-page", method = "client-command")
public class OpenWebPageHandler implements IJcefActionHandler {

    private static final Logger log = LoggerFactory.getLogger(OpenWebPageHandler.class);

    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        JSONObject message = JSON.parseObject(consoleMessage.getMessage());
        String url = message.getString("url");

        log.info("Received request to open web page: {}", url);

        boolean opened = false;
        String errorMessage = null;

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new java.net.URI(url));
                opened = true;
                log.info("Successfully requested the default system browser to open URL: {}", url);
            } else {
                errorMessage = "Opening a browser is not supported on the current system";
                log.warn(errorMessage);
            }
        } catch (java.net.URISyntaxException e) {
            errorMessage = "Invalid URL syntax: " + url;
            log.error(errorMessage, e);
        } catch (IOException e) {
            errorMessage = "I/O error while opening web page: " + url;
            log.error(errorMessage, e);
        } catch (Exception e) {
            errorMessage = "Unexpected error while opening web page: " + url;
            log.error(errorMessage, e);
        }

        if (opened) {
            ResponseBuilder.buildSuccessJcef(Map.of("success", true, "message", "Web page opened in the system browser"), callback);
        } else {
            ResponseBuilder.buildSuccessJcef(Map.of("success", false, "errorMessage", errorMessage), callback);
        }
    }
}
