package quantum.browser.ui;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

public class Tab extends JPanel {
    private TabManager manager;
    final CefBrowser browser;
    final CefClient client;
    String title = "Loading...";

    public Tab(final TabManager manager, CefClient client) {
        super(new BorderLayout());
        this.manager = manager;
        this.client = client;

        browser = client.createBrowser("https://dmoj.ca/", manager.osrEnabled, false);
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

        /*new Thread() {
            @Override
            public void run() {
                try {
                    Method method = browser.getClass().getDeclaredMethod("doUpdate");
                    method.setAccessible(true);
                    while (true) {
                        if (Tab.this != manager.currentTab()) {
                            method.invoke(browser);
                            System.out.println("Invoke");
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }
}
