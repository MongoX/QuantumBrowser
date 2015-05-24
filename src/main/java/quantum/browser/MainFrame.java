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
    protected final CefClient client;
    protected final CefBrowser browser;
    protected final Component browserUI;
    protected final CefApp app;

    public MainFrame(boolean osrEnabled) {
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = osrEnabled;
        settings.background_color = settings.new ColorType(100, 255, 242, 211);

        app = CefApp.getInstance(settings);
        CefApp.addAppHandler(new AppHandler());

        System.out.println(app.getVersion());

        client = app.createClient();
        browser = client.createBrowser("https://dmoj.ca/", osrEnabled, false);
        browserUI = browser.getUIComponent();

        getContentPane().add(browserUI, BorderLayout.CENTER);
        setSize(Settings.getDimension("window_size", new Dimension(800, 600)));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Settings.putDimension("window_size", getSize());
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }
}
