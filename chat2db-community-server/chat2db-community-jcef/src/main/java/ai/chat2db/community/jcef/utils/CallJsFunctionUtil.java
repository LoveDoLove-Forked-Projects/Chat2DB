package ai.chat2db.community.jcef.utils;


import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;


@Slf4j
public class CallJsFunctionUtil {


    public static void callHandleJavaMessage(CefBrowser browser, String dataToSend) {
        if (browser == null) {
            log.error("CefBrowser instance is null. Cannot execute JavaScript.");
            return;
        }
        String escapedData = escapeStringForJavaScript(dataToSend);
        String script = String.format(
                "if (typeof handleJavaMessage === 'function') {" +
                        "  handleJavaMessage('%s');" +
                        "} else {" +
                        "  console.error('JavaScript function handleJavaMessage not found.');" +
                        "}",
                escapedData
        );
        browser.executeJavaScript(script, browser.getURL(), 0);
    }


    private static String escapeStringForJavaScript(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }
}
