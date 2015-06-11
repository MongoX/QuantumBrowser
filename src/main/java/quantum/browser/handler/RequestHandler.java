package quantum.browser.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefAllowCertificateErrorCallback;
import org.cef.callback.CefAuthCallback;
import org.cef.handler.CefLoadHandler;
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
        new Thread(new PasswordDialog(frame, callback)).start();
        return true;
    }

    @Override
    public boolean onCertificateError(final CefLoadHandler.ErrorCode cert_error, final String request_url, final CefAllowCertificateErrorCallback callback) {
        new Thread() {
            @Override
            public void run() {
                callback.Continue(JOptionPane.showConfirmDialog(frame,
                        "An certificate error (" + cert_error + ") occurreed " +
                                "while requesting\n" + request_url +
                                "\nDo you want to proceed anyway?",
                        "Certificate error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION);
            }
        }.start();
        return true;
    }
}
