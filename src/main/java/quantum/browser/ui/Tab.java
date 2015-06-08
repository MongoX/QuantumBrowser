package quantum.browser.ui;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefQueryCallback;
import org.cef.callback.CefStringVisitor;
import org.cef.handler.*;
import quantum.browser.data.Settings;
import quantum.browser.dialog.ViewSourceDialog;
import quantum.browser.handler.RequestHandler;
import quantum.browser.utils.Resources;
import quantum.browser.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.MalformedURLException;
import java.net.URL;

import static org.cef.callback.CefContextMenuParams.EditStateFlags.*;
import static org.cef.callback.CefContextMenuParams.TypeFlags.*;
import static org.cef.callback.CefMenuModel.MenuId.*;

public class Tab extends JPanel {
    protected TabManager manager;
    protected JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    protected CefBrowser devTools;
    protected FindPanel findPanel;
    final CefBrowser browser;
    final CefClient client;
    String title = "Loading...";
    String statusText;
    int loadProgress = 0;

    public Tab(final TabManager manager, CefClient client, String url) {
        super(new BorderLayout());
        this.manager = manager;
        this.client = client;

        browser = client.createBrowser(url, manager.osrEnabled, false, manager.getRequestContext());
        browser.setWindowVisibility(false);

        splitPane.setTopComponent(browser.getUIComponent());
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        add(splitPane, BorderLayout.CENTER);

        client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean doClose(CefBrowser browser) {
                return true;
            }
        });

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, String url) {
                if (browser != Tab.this.browser) return;
                title = "Loading...";
                manager.setTitleAt(manager.indexOfComponent(Tab.this), "Loading...");
                manager.updateAddress(Tab.this);
            }

            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                if (browser != Tab.this.browser) return;
                Tab.this.title = title;
                manager.setTitleAt(manager.indexOfComponent(Tab.this), title);
                manager.updateNavigation(Tab.this);
            }

            @Override
            public void onStatusMessage(CefBrowser browser, String value) {
                if (browser != Tab.this.browser) return;
                statusText = value;
                manager.updateNavigation(Tab.this);
            }
        });

        final Timer timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProgress += (1000 - loadProgress) / 3;
                manager.updateLoadStatus(Tab.this);
            }
        });

        final String faviconJS = Resources.readResource("quantum/browser/favicon.js");
        client.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (browser != Tab.this.browser) return;
                if (isLoading) {
                    loadProgress = 0;
                    timer.start();
                    title = "Loading...";
                    manager.setTitleAt(manager.indexOfComponent(Tab.this), "Loading...");
                } else {
                    loadProgress = 1000;
                    timer.stop();
                }
                manager.updateLoadStatus(Tab.this);
                manager.updateNavigation(Tab.this);
            }

            /*@Override
            public void onLoadEnd(CefBrowser browser, int frameIdentifier, int httpStatusCode) {
                if (browser != Tab.this.browser) return;
                browser.executeJavaScript(faviconJS, "chrome://favicon.js", 0);
            }

            @Override
            public void onLoadStart(CefBrowser browser, int frameIdentifer) {
                ImageIcon icon = manager.favicon.getFavicon(Utils.getDomain(browser.getURL()), null);
                System.out.println(icon);
            }*/
        });

        client.addKeyboardHandler(new CefKeyboardHandlerAdapter() {
            @Override
            public boolean onKeyEvent(CefBrowser browser, CefKeyEvent event) {
                if (browser != Tab.this.browser)
                    return false;
                if (event.type == CefKeyEvent.EventType.KEYEVENT_KEYUP) {
                    switch (event.windows_key_code) {
                        case 123:
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    showDevTools();
                                }
                            });
                            return true;
                        case 'F':
                            if (event.modifiers == CefContextMenuHandler.EventFlags.EVENTFLAG_CONTROL_DOWN) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        showFind();
                                    }
                                });
                                return true;
                            }
                            break;
                        case 'N':
                        case 'T':
                            if (event.modifiers == CefContextMenuHandler.EventFlags.EVENTFLAG_CONTROL_DOWN) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        manager.newTab();
                                    }
                                });
                                return true;
                            }
                    }
                }
                return false;
            }
        });

        client.addMessageRouter(CefMessageRouter.create(new CefMessageRouterHandlerAdapter() {
            @Override
            public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {
                if (request.startsWith("favicon:")) {
                    String[] data = request.split("\001", -1);
                    if (data.length != 3 || !"favicon:".equals(data[0]))
                        return false;
                    if (browser.getURL().equals(data[1])) {
                        if (data[2].isEmpty()) {
                            try {
                                System.out.println("Favicon (default): " + new URL(new URL(browser.getURL()), "/favicon.ico").toExternalForm());
                            } catch (MalformedURLException ignored) {}
                        } else {
                            try {
                                URL favicon = new URL(new URL(browser.getURL()), data[2]);
                                ImageIcon icon = manager.favicon.getFavicon(Utils.getDomain(browser.getURL()), favicon);
                                System.out.println(icon);
                                System.out.println("Favicon: " + favicon.toExternalForm());
                            } catch (MalformedURLException ignored) {}
                        }
                    }
                }
                return false;
            }
        }));

        client.addRequestHandler(new RequestHandler(manager.owner));

        client.addContextMenuHandler(new CefContextMenuHandlerAdapter() {
            public final int MENU_ID_OPEN_LINK = MENU_ID_USER_FIRST;
            public final int MENU_ID_NEW_TAB = MENU_ID_USER_FIRST + 1;
            public final int MENU_ID_COPY_LINK = MENU_ID_USER_FIRST + 2;
            public final int MENU_ID_DOWNLOAD_IMAGE = MENU_ID_USER_FIRST + 3;

            @Override
            public void onBeforeContextMenu(CefBrowser browser, CefContextMenuParams params, CefMenuModel model) {
                model.clear();
                if ((params.getTypeFlags() & (CM_TYPEFLAG_SELECTION |
                                              CM_TYPEFLAG_EDITABLE)) != 0) {
                    if (params.isEditable()) {
                        model.addItem(MENU_ID_UNDO, "&Undo");
                        model.addItem(MENU_ID_REDO, "&Redo");
                        model.addSeparator();
                        model.addItem(MENU_ID_CUT, "Cu&t");
                    }
                    model.addItem(MENU_ID_COPY, "&Copy");
                    if (params.isEditable()) {
                        model.addItem(MENU_ID_PASTE, "&Paste");
                        model.addItem(MENU_ID_DELETE, "&Delete");
                        model.addSeparator();
                        model.addItem(MENU_ID_SELECT_ALL, "&Select all");
                    }
                    model.setEnabled(MENU_ID_UNDO, (params.getEditStateFlags() & CM_EDITFLAG_CAN_UNDO) != 0);
                    model.setEnabled(MENU_ID_REDO, (params.getEditStateFlags() & CM_EDITFLAG_CAN_REDO) != 0);
                    model.setEnabled(MENU_ID_CUT, (params.getEditStateFlags() & CM_EDITFLAG_CAN_CUT) != 0);
                    model.setEnabled(MENU_ID_COPY, (params.getEditStateFlags() & CM_EDITFLAG_CAN_COPY) != 0);
                    model.setEnabled(MENU_ID_PASTE, (params.getEditStateFlags() & CM_EDITFLAG_CAN_PASTE) != 0);
                    model.setEnabled(MENU_ID_DELETE, (params.getEditStateFlags() & CM_EDITFLAG_CAN_DELETE) != 0);
                    model.setEnabled(MENU_ID_SELECT_ALL, (params.getEditStateFlags() & CM_EDITFLAG_CAN_SELECT_ALL) != 0);
                } else if ((params.getTypeFlags() & (CM_TYPEFLAG_LINK | CM_TYPEFLAG_MEDIA)) != 0) {
                    if ((params.getTypeFlags() & CM_TYPEFLAG_LINK) != 0) {
                        model.addItem(MENU_ID_OPEN_LINK, "&Follow");
                        model.addItem(MENU_ID_NEW_TAB, "Follow in &new tab");
                        model.addSeparator();
                        model.addItem(MENU_ID_COPY_LINK, "&Copy link address");
                    }
                    if ((params.getTypeFlags() & (CM_TYPEFLAG_LINK | CM_TYPEFLAG_MEDIA)) != (CM_TYPEFLAG_LINK | CM_TYPEFLAG_MEDIA))
                        model.addSeparator();
                    if ((params.getTypeFlags() & CM_TYPEFLAG_MEDIA) != 0)
                        model.addItem(MENU_ID_DOWNLOAD_IMAGE, "&Download image");
                } else {
                    model.addItem(MENU_ID_BACK, "&Back");
                    model.addItem(MENU_ID_FORWARD, "&Forward");
                    model.addItem(MENU_ID_RELOAD, "&Reload");
                    model.addItem(MENU_ID_STOPLOAD, "&Stop");
                    model.addSeparator();
                    model.addItem(MENU_ID_PRINT, "&Print...");
                    model.addSeparator();
                    model.addItem(MENU_ID_VIEW_SOURCE, "&View source");
                    model.setEnabled(MENU_ID_BACK, browser.canGoBack());
                    model.setEnabled(MENU_ID_FORWARD, browser.canGoForward());
                    model.setEnabled(MENU_ID_RELOAD, !browser.isLoading());
                    model.setEnabled(MENU_ID_STOPLOAD, browser.isLoading());
                }
            }

            @Override
            public boolean onContextMenuCommand(CefBrowser browser, CefContextMenuParams params, int commandId, int eventFlags) {
                switch (commandId) {
                    case MENU_ID_OPEN_LINK:
                        browser.loadURL(params.getLinkUrl());
                        return true;
                    case MENU_ID_NEW_TAB:
                        manager.newTab(params.getLinkUrl());
                        return true;
                    case MENU_ID_COPY_LINK:
                        StringSelection selection = new StringSelection(params.getUnfilteredLinkUrl());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                        return true;
                    case MENU_ID_DOWNLOAD_IMAGE:
                        browser.startDownload(params.getSourceUrl());
                        return true;
                    case MENU_ID_VIEW_SOURCE:
                        final String url = browser.getURL();
                        browser.getSource(new CefStringVisitor() {
                            @Override
                            public void visit(String string) {
                                SwingUtilities.invokeLater(new ViewSourceDialog(manager.owner, url, string));
                            }
                        });
                        return true;
                    default:
                        return false;
                }
            }
        });

        client.addDownloadHandler(manager.download);
    }

    public void showFind() {
        if (findPanel == null)
            findPanel = new FindPanel(this);
        add(findPanel, BorderLayout.SOUTH);
        findPanel.activate();
        revalidate();
    }

    public void hideFind() {
        remove(findPanel);
        revalidate();
        browser.stopFinding(false);
        splitPane.getTopComponent().requestFocusInWindow();
    }

    public void showDevTools() {
        if (devTools != null) return;
        devTools = browser.getDevTools();
        final Component devToolsUI = devTools.getUIComponent();
        devToolsUI.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (devToolsUI.getHeight() < 10)
                    hideDevTools();
            }
        });
        splitPane.setEnabled(true);
        splitPane.setDividerSize(2);
        splitPane.setDividerLocation(Math.max(splitPane.getHeight() - 200, 50));
        splitPane.setBottomComponent(devToolsUI);
    }

    public void hideDevTools() {
        if (devTools == null) return;
        devTools.close();
        devTools = null;
        splitPane.setBottomComponent(null);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.getTopComponent().requestFocusInWindow();
    }

    public void close() {
        browser.close();
        client.dispose();
        manager.remove(this);
    }
}
