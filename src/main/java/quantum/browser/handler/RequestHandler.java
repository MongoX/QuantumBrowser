package quantum.browser.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefAuthCallback;
import org.cef.handler.CefRequestHandlerAdapter;
import quantum.browser.dialog.PasswordDialog;
import quantum.browser.ui.MainFrame;

import javax.swing.*;

public class RequestHandler extends CefRequestHandlerAdapter {
    private final MainFrame frame;

    public RequestHandler(MainFrame frame) {

        this.frame = frame;
    }

    @Override
    public boolean getAuthCredentials(CefBrowser browser, boolean isProxy, String host, int port, String realm, String scheme, CefAuthCallback callback) {
        SwingUtilities.invokeLater(new PasswordDialog(frame, callback));
        return true;
    }
}
