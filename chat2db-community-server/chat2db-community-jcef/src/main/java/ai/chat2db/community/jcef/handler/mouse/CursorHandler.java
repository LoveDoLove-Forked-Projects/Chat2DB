package ai.chat2db.community.jcef.handler.mouse;

import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.*;
import java.awt.*;

public class CursorHandler extends CefDisplayHandlerAdapter {

    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        Cursor awtCursor = Cursor.getPredefinedCursor(cursorType);
        SwingUtilities.invokeLater(() -> {
            Component uiComponent = browser.getUIComponent();
            if (uiComponent != null) {
                uiComponent.setCursor(awtCursor);
            }
        });
        return false;
    }
}
