package ai.chat2db.community.jcef.handler.keyboard;

import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.frame.MainJFrame;
import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefKeyboardHandlerAdapter;

import java.awt.event.KeyEvent;

import static org.cef.misc.EventFlags.*;

@Slf4j
public class KeyboardHandler extends CefKeyboardHandlerAdapter {
    private final MainJFrame owner_;

    public KeyboardHandler() {
        this.owner_ = JcefContext.getInstance().getFrame_();
    }

    @Override
    public boolean onKeyEvent(CefBrowser browser, CefKeyEvent event) {
        if (event.type == CefKeyEvent.EventType.KEYEVENT_RAWKEYDOWN || event.type == CefKeyEvent.EventType.KEYEVENT_KEYDOWN) {
            boolean shiftDown = (event.modifiers & EVENTFLAG_SHIFT_DOWN) != 0;
            boolean altDown = (event.modifiers & EVENTFLAG_ALT_DOWN) != 0;
            boolean controlDown = (event.modifiers & EVENTFLAG_CONTROL_DOWN) != 0;
            boolean commandDown = (event.modifiers & EVENTFLAG_COMMAND_DOWN) != 0;

            boolean controlOrCommandDown;
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac")) {
                controlOrCommandDown = commandDown;
            } else {
                controlOrCommandDown = controlDown;
            }

            int keyCode = event.windows_key_code;
            log.info("onPreKeyEvent: key code: {}, controlOrCommandDown: {}", keyCode, controlOrCommandDown);
            if ((keyCode == KeyEvent.VK_F12 && !controlOrCommandDown && !shiftDown && !altDown) ||
                    (controlOrCommandDown && shiftDown && keyCode == KeyEvent.VK_I)) {
                owner_.toggleDevTools();
                return true;
            }
            if (controlOrCommandDown && !altDown) {
                CefFrame focusedFrame = browser.getFocusedFrame();
                if (focusedFrame != null) {
                    switch (keyCode) {
                        case KeyEvent.VK_C:
                            if (!shiftDown) {
                                focusedFrame.copy();
                                return true;
                            }
                            break;
                        case KeyEvent.VK_V:
                            if (!shiftDown) {
                                focusedFrame.paste();
                                return true;
                            }
                            break;
                        case KeyEvent.VK_X:
                            if (!shiftDown) {
                                focusedFrame.cut();
                                return true;
                            }
                            break;
                        case KeyEvent.VK_A:
                            if (!shiftDown) {
                                focusedFrame.selectAll();
                                return true;
                            }
                            break;
                        case KeyEvent.VK_Z:
                            if (shiftDown) {
                                focusedFrame.redo();
                            } else {
                                focusedFrame.undo();
                            }
                            return true;
                        case KeyEvent.VK_Y:
                            if (!osName.contains("mac") && !shiftDown) {
                                focusedFrame.redo();
                                return true;
                            }
                            break;
                    }
                }
            }
            if (keyCode == KeyEvent.VK_F5 && !controlOrCommandDown && !shiftDown && !altDown) {
                browser.reloadIgnoreCache();
                return true;
            }
            if (controlOrCommandDown && keyCode == KeyEvent.VK_R) {
                if (shiftDown) {
                    browser.reloadIgnoreCache();
                } else {
                    browser.reload();
                }
                return true;
            }
            if (controlOrCommandDown && keyCode == KeyEvent.VK_P && !shiftDown) {
                browser.print();
                return true;
            }
        }
        return false;
    }
}
