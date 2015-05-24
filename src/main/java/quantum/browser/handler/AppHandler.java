package quantum.browser.handler;

import org.cef.CefApp;
import org.cef.handler.CefAppHandlerAdapter;

public class AppHandler extends CefAppHandlerAdapter {
    public AppHandler() {
        super(null);
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState state) {
        if (state == CefApp.CefAppState.TERMINATED)
            System.exit(0);
    }
}
