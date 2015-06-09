package quantum.browser.dialog;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import quantum.browser.handler.AppHandler;
import quantum.browser.natives.NativeLoader;
import quantum.browser.ui.MainFrame;
import quantum.browser.utils.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.net.URL;


public class ViewSourceDialog extends JDialog implements Runnable {
    public ViewSourceDialog(MainFrame owner, String url, final String content) {
        super(owner, "View Source: " + url, false);
        setLayout(new BorderLayout());
        setSize(new Dimension(640, 480));
        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setEditable(false);
        textArea.setText(content);
        add(new RTextScrollPane(textArea));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void run() {
        setVisible(true);
    }

    public static void main(String... args) throws Exception {
        System.out.print("Loading...");
        InputStream source = new URL("http://en.wikipedia.org/wiki/Main_Page").openStream();
        String html = Resources.toString(source);
        System.out.println(" Done");
        System.out.println(html);
        System.out.println(html.length());
        SwingUtilities.invokeLater(new ViewSourceDialog(null, "http://en.wikipedia.org/wiki/Main_Page", html));
    }
}
