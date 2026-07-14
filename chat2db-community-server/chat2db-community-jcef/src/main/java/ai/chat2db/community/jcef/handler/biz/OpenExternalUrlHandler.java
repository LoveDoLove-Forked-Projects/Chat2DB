package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cef.callback.CefQueryCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@JcefAction(value = "api/user/authenticate", method = "post")
public class OpenExternalUrlHandler implements IJcefActionHandler {

    Logger log = LoggerFactory.getLogger(OpenExternalUrlHandler.class);

    private final ObjectMapper objectMapper;

    public OpenExternalUrlHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(consoleMessage.getMessage());
        String urlToOpen = jsonNode.get("urlToOpen").textValue();
        log.info("Received request from JS to open external link: {}", urlToOpen);
        boolean opened = false;
        String errorMessage = null;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new java.net.URI(urlToOpen));
                opened = true;
                log.info("Successfully requested the default system browser to open URL: {}", urlToOpen);
            } else {
                errorMessage = "Desktop.browse is not supported on the current system.";
                log.warn(errorMessage);
            }
        } catch (java.net.URISyntaxException e) {
            errorMessage = "Invalid URL syntax: " + urlToOpen;
            log.error(errorMessage, e);
        } catch (IOException e) {
            errorMessage = "I/O error while opening external link: " + urlToOpen;
            log.error(errorMessage, e);
        } catch (Exception e) {
            errorMessage = "Unexpected error while opening external link: " + urlToOpen;
            log.error(errorMessage, e);
        }

        Map<String,Object> responseJson = new HashMap<>();
        if (opened) {
            responseJson.put("success", true);
            responseJson.put("message", "External link opened in the system browser.");
        } else {
            responseJson.put("success", false);
            responseJson.put("errorMessage", errorMessage);
        }
        String result = JSON.toJSONString(responseJson);
        wsResult.setMessage(responseJson);
        callback.success(new String(result.getBytes(), StandardCharsets.UTF_8));
    }
}
