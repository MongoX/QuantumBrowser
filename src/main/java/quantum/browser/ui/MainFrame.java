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
    final CefApp app;
    final ToolBar toolBar;
    final MenuBar menubar;
    final TabManager tabManager;

    public MainFrame(boolean osrEnabled) {
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = osrEnabled;
        settings.cache_path = Settings.cacheDirectory.getAbsolutePath();
        settings.log_file = Settings.logFile.getAbsolutePath();
        settings.background_color = settings.new ColorType(255, 255, 255, 255);

        app = CefApp.getInstance(settings);
        CefApp.addAppHandler(new AppHandler());
        //app.createClient().dispose();

        System.out.println(app.getVersion());

        tabManager = new TabManager(this, osrEnabled);
        menubar = new MenuBar(this);
        toolBar = new ToolBar(this);
        setJMenuBar(menubar);
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(tabManager, BorderLayout.CENTER);
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

        tabManager.newTab();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title + " - Quantum Browser");
    }
}
