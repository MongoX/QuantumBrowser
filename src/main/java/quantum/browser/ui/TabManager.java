package quantum.browser.ui;

import org.cef.browser.CefRequestContext;
import org.cef.handler.CefRequestContextHandler;
import org.cef.network.CefCookieManager;
import quantum.browser.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabManager extends JTabbedPane {
    private MainFrame owner;
    boolean osrEnabled;
    private CefCookieManager cookieManager;
    private CefRequestContext requestContext;
    private ToolBar toolBar;

    public TabManager(final MainFrame owner, boolean osrEnabled) {
        this.owner = owner;
        this.osrEnabled = osrEnabled;

        System.out.println(Settings.cookieDirectory.getAbsolutePath());
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateNavigation(currentTab());
            }
        });
    }

    public CefCookieManager getCookieManager() {
        if (cookieManager == null)
            cookieManager = CefCookieManager.createManager(Settings.cookieDirectory.getAbsolutePath(), false);
        return cookieManager;
    }

    public CefRequestContext getRequestContext() {
        if (cookieManager == null)
            requestContext = CefRequestContext.createContext(new CefRequestContextHandler() {
                @Override
                public CefCookieManager getCookieManager() {
                    return TabManager.this.getCookieManager();
                }
            });
        return requestContext;
    }

    public void updateNavigation(Tab tab) {
        if (getSelectedComponent() == tab) {
            owner.setTitle(tab.title);
            owner.toolBar.addressBar.setText(tab.browser.getURL());
            owner.toolBar.backButton.setEnabled(tab.browser.canGoBack());
            owner.toolBar.forwardButton.setVisible(tab.browser.canGoForward());
            boolean loading = tab.browser.isLoading();
            owner.toolBar.stopButton.setVisible(loading);
            owner.toolBar.refreshButton.setVisible(!loading);
        }
    }

    public void newTab() {
        Tab tab = new Tab(this, owner.app.createClient());
        insertTab("Loading...", null, tab, null, getSelectedIndex() + 1);
        setSelectedComponent(tab);
    }

    public Tab currentTab() {
        return (Tab) getSelectedComponent();
    }
}
