package ai.chat2db.community.jcef.handler.biz;

import ai.chat2db.community.jcef.annotation.JcefAction;
import ai.chat2db.community.jcef.builder.ResponseBuilder;
import ai.chat2db.community.tools.console.ConsoleMessage;
import ai.chat2db.community.tools.console.ConsoleResult;
import lombok.extern.slf4j.Slf4j;
import org.cef.callback.CefQueryCallback;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Map;

@Slf4j
@JcefAction(value = "read-clipboard", method = "client-command")
public class ReadClipboardContentHandler implements IJcefActionHandler {

    @Override
    public void handle(ConsoleMessage consoleMessage, ConsoleResult wsResult, CefQueryCallback callback) throws Exception {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String pastedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                ResponseBuilder.buildSuccessJcef(Map.of("data", pastedText), callback);
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
            }
        } else {
            ResponseBuilder.buildSuccessJcef(Map.of("data", ""), callback);
        }
    }
}
