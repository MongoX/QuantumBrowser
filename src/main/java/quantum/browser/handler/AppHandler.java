package quantum.browser.handler;

import org.cef.CefApp;
import org.cef.handler.CefAppHandlerAdapter;

public class AppHandler extends CefAppHandlerAdapter {
    public AppHandler() {
        super(null);
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState state) {
        // Shutdown the app if the native CEF part is terminated
        if (state == CefApp.CefAppState.TERMINATED)
            System.exit(0);
    }
}
