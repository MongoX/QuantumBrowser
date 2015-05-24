package quantum.browser.ui;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandler;
import org.cef.handler.CefDisplayHandlerAdapter;
import quantum.browser.Settings;
import quantum.browser.handler.AppHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    final CefClient client;
    final CefBrowser browser;
    final Component browserUI;
    final CefApp app;
    final ToolBar toolBar;
    final MenuBar menubar;

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

        menubar = new MenuBar();
        toolBar = new ToolBar(this);
        setJMenuBar(menubar);
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(browserUI, BorderLayout.CENTER);
        pack();
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
