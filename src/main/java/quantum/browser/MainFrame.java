package quantum.browser;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import quantum.browser.handler.AppHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    protected CefClient client;
    protected CefBrowser browser;
    protected Component browserUI;

    public MainFrame(boolean osrEnabled) {
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = osrEnabled;
        settings.background_color = settings.new ColorType(100, 255, 242, 211);

        CefApp app = CefApp.getInstance(settings);
        CefApp.addAppHandler(new AppHandler());

        CefApp.CefVersion version = app.getVersion();
        System.out.println("Using:\n" + version);

        client = app.createClient();
        browser = client.createBrowser("https://dmoj.ca/", osrEnabled, false);
        browserUI = browser.getUIComponent();

        getContentPane().add(browserUI, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }
}
