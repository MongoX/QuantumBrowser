package quantum.browser.ui;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.*;
import quantum.browser.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Tab extends JPanel {
    protected TabManager manager;
    protected JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    protected CefBrowser devTools;
    final CefBrowser browser;
    final CefClient client;
    String title = "Loading...";
    String statusText;
    int loadProgress = 0;
    boolean loading = false;

    public Tab(final TabManager manager, CefClient client) {
        super(new BorderLayout());
        this.manager = manager;
        this.client = client;

        browser = client.createBrowser(Settings.get("home_page", "https://dmoj.ca/"),
                manager.osrEnabled, false, manager.getRequestContext());
        browser.setWindowVisibility(false);

        splitPane.setTopComponent(browser.getUIComponent());
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        add(splitPane, BorderLayout.CENTER);

        client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean doClose(CefBrowser browser) {
                return true;
            }
        });

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, String url) {
                if (browser != Tab.this.browser) return;
                title = "Loading...";
                manager.setTitleAt(manager.indexOfComponent(Tab.this), "Loading...");
                manager.updateNavigation(Tab.this);
            }

            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                if (browser != Tab.this.browser) return;
                Tab.this.title = title;
                manager.setTitleAt(manager.indexOfComponent(Tab.this), title);
                manager.updateNavigation(Tab.this);
            }

            @Override
            public void onStatusMessage(CefBrowser browser, String value) {
                if (browser != Tab.this.browser) return;
                statusText = value;
                manager.updateNavigation(Tab.this);
            }
        });

        final Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProgress += (1000 - loadProgress) / 3;
                manager.updateLoadStatus(Tab.this);
            }
        });

        client.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (browser != Tab.this.browser) return;
                if (isLoading) {
                    loadProgress = 0;
                    timer.start();
                    title = "Loading...";
                    manager.setTitleAt(manager.indexOfComponent(Tab.this), "Loading...");
                } else {
                    loadProgress = 1000;
                    timer.stop();
                }
                loading = isLoading;
                manager.updateLoadStatus(Tab.this);
                manager.updateNavigation(Tab.this);
            }
        });

        client.addKeyboardHandler(new CefKeyboardHandlerAdapter() {
            @Override
            public boolean onKeyEvent(CefBrowser browser, CefKeyEvent event) {
                if (browser == Tab.this.browser &&
                        event.type == CefKeyEvent.EventType.KEYEVENT_KEYUP &&
                        event.windows_key_code == 123)
                    showDevTools();
                return true;
            }
        });
    }

    public void showDevTools() {
        if (devTools != null) return;
        devTools = browser.getDevTools();
        final Component devToolsUI = devTools.getUIComponent();
        devToolsUI.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (devToolsUI.getHeight() < 10)
                    hideDevTools();
            }
        });
        splitPane.setEnabled(true);
        splitPane.setDividerSize(2);
        splitPane.setDividerLocation(Math.max(splitPane.getHeight() - 200, 50));
        splitPane.setBottomComponent(devToolsUI);
    }

    public void hideDevTools() {
        if (devTools == null) return;
        devTools.close();
        devTools = null;
        splitPane.setBottomComponent(null);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
    }

    public void close() {
        browser.close();
        client.dispose();
        manager.remove(this);
    }
}
