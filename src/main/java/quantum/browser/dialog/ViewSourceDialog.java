package quantum.browser.dialog;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import quantum.browser.handler.AppHandler;
import quantum.browser.natives.NativeLoader;
import quantum.browser.ui.MainFrame;
import quantum.browser.utils.Resources;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;


public class ViewSourceDialog extends JDialog implements Runnable {
    public ViewSourceDialog(MainFrame owner, String url, final String content) {
        super(owner, "View Source: " + url, false);
        setLayout(new BorderLayout());
        setSize(new Dimension(640, 480));

        CefClient client = CefApp.getInstance().createClient();
        client.addMessageRouter(CefMessageRouter.create(new CefMessageRouterHandlerAdapter() {
            @Override
            public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {
                callback.success(content);
                return true;
            }
        }));
        CefBrowser browser = client.createBrowser("quantum://resources/prism.html", false, false);
        add(browser.getUIComponent(), BorderLayout.CENTER);
        System.out.println("Done");
    }

    @Override
    public void run() {
        setVisible(true);
    }

    public static void main(String... args) throws Exception {
        NativeLoader.unpack();
        CefApp.addAppHandler(new AppHandler());

        System.out.print("Loading...");
        InputStream source = new URL("http://en.wikipedia.org/wiki/Main_Page").openStream();
        String html = Resources.toString(source);
        System.out.println(" Done");
        System.out.println(html);
        System.out.println(html.length());
        SwingUtilities.invokeLater(new ViewSourceDialog(null, "http://en.wikipedia.org/wiki/Main_Page", html));
    }
}
