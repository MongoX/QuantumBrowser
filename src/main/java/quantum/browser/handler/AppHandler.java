package quantum.browser.handler;

import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

public class AppHandler extends CefAppHandlerAdapter {
    public AppHandler() {
        super(null);
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState state) {
        if (state == CefApp.CefAppState.TERMINATED)
            System.exit(0);
    }

    @Override
    public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
        registrar.addCustomScheme(QuantumSchemeHandler.scheme, true, true, false);
    }

    @Override
    public void onContextInitialized() {
        CefApp.getInstance().registerSchemeHandlerFactory(QuantumSchemeHandler.scheme, QuantumSchemeHandler.domain, new CefSchemeHandlerFactory() {
            @Override
            public CefResourceHandler create(CefBrowser browser, String schemeName, CefRequest request) {
                return new QuantumSchemeHandler();
            }
        });
    }
}
