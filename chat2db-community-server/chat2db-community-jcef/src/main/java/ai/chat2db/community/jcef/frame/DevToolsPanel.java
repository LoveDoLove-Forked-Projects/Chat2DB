package ai.chat2db.community.jcef.frame;

import lombok.Setter;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;

public class DevToolsPanel extends JPanel {
    private final CefBrowser mainBrowser;
    private CefBrowser devTools;
    private Component devToolsUI;
    private JButton closeButton;
    @Setter
    private IDevToolsCloseListener closeListener;
    public interface IDevToolsCloseListener {
        void onDevToolsClosed();
    }

    public DevToolsPanel(CefBrowser mainBrowser) {
        super(new BorderLayout());
        this.mainBrowser = mainBrowser;
        setBorder(null);
        setVisible(false);
    }

    public void activateDevTools() {
        if (devTools == null) {
            this.devTools = mainBrowser.getDevTools();
            this.devToolsUI = devTools.getUIComponent();
            add(devToolsUI, BorderLayout.CENTER);
        }
        setVisible(true);
    }

    public void deactivateDevTools() {
        setVisible(false);
        if (devTools != null) {
            if (devToolsUI != null) {
                remove(devToolsUI);
            }
            devTools.close(true);
            devTools = null;
            devToolsUI = null;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getParent().getWidth() / 3, getParent().getHeight());
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(400, 600);
    }
}
