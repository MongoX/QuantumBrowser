package quantum.browser.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefGeolocationCallback;
import org.cef.handler.CefGeolocationHandlerAdapter;
import quantum.browser.ui.MainFrame;

import javax.swing.*;

public class GeolocationHandler extends CefGeolocationHandlerAdapter {
    private final MainFrame frame;

    public GeolocationHandler(MainFrame frame) {
        this.frame = frame;
    }

    @Override
    public boolean onRequestGeolocationPermission(CefBrowser browser, final String url, int id, final CefGeolocationCallback callback) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                callback.Continue(JOptionPane.showConfirmDialog(frame,
                        "The following page requests to know your location:\n" + url +
                                "\nDo you want to provide such information?",
                        "Geolocation request",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION);
            }
        });
        return true;
    }
}
