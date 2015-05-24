package quantum.browser.ui;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.*;
import java.awt.*;

public class Tab extends JPanel {
    private TabManager manager;
    final CefBrowser browser;
    final CefClient client;
    String title = "Loading...";

    public Tab(final TabManager manager, CefClient client) {
        super(new BorderLayout());
        this.manager = manager;
        this.client = client;

        browser = client.createBrowser("https://dmoj.ca/", manager.osrEnabled, false, manager.getRequestContext());
        browser.setWindowVisibility(false);

        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, String url) {
                title = "Loading...";
                manager.setTitleAt(manager.indexOfComponent(Tab.this), "Loading...");
                manager.updateNavigation(Tab.this);
            }

            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                Tab.this.title = title;
                manager.setTitleAt(manager.indexOfComponent(Tab.this), title);
                manager.updateNavigation(Tab.this);
            }
        });

    }
}
