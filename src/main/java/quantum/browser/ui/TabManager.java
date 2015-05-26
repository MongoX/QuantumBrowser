package quantum.browser.ui;

import org.cef.browser.CefRequestContext;
import org.cef.handler.CefRequestContextHandler;
import org.cef.network.CefCookieManager;
import quantum.browser.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabManager extends JTabbedPane {
    private MainFrame owner;
    boolean osrEnabled;
    private CefCookieManager cookieManager;
    private CefRequestContext requestContext;
    private ToolBar toolBar;

    public TabManager(final MainFrame owner, boolean osrEnabled) {
        this.owner = owner;
        this.osrEnabled = osrEnabled;

        System.out.println(Settings.cookieDirectory.getAbsolutePath());
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateNavigation(currentTab());
                updateLoadStatus(currentTab());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final int index = getUI().tabForCoordinate(TabManager.this, e.getX(), e.getY());
                if (index == -1)
                    return;
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    if (getTabComponentAt(index) instanceof TabLabel)
                        ((TabLabel) getTabComponentAt(index)).getCloseButton().doClick();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    e.consume();
            }
        });
    }

    public CefCookieManager getCookieManager() {
        if (cookieManager == null)
            cookieManager = CefCookieManager.createManager(Settings.cookieDirectory.getAbsolutePath(), false);
        return cookieManager;
    }

    public CefRequestContext getRequestContext() {
        if (cookieManager == null)
            requestContext = CefRequestContext.createContext(new CefRequestContextHandler() {
                @Override
                public CefCookieManager getCookieManager() {
                    return TabManager.this.getCookieManager();
                }
            });
        return requestContext;
    }

    public void updateLoadStatus(Tab tab) {
        if (tab != null && getSelectedComponent() == tab) {
            owner.statusBar.setProgress(tab.loadProgress);
            owner.statusBar.setVisible(tab.loading);
        }
    }

    public void updateNavigation(Tab tab) {
        if (tab != null && getSelectedComponent() == tab) {
            owner.setTitle(tab.title);
            owner.toolBar.addressBar.setText(tab.browser.getURL());
            owner.toolBar.backButton.setEnabled(tab.browser.canGoBack());
            owner.toolBar.forwardButton.setVisible(tab.browser.canGoForward());
            boolean loading = tab.browser.isLoading();
            owner.toolBar.stopButton.setVisible(loading);
            owner.toolBar.refreshButton.setVisible(!loading);
            owner.statusBar.setStatus(tab.statusText);
        }
    }

    public void newTab() {
        final Tab tab = new Tab(this, owner.app.createClient());
        insertTab("Loading...", null, tab, null, getSelectedIndex() + 1);
        setSelectedComponent(tab);
        setTabComponentAt(indexOfComponent(tab), new TabLabel("Loading...") {{
            getCloseButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tab.close();
                }
            });
        }});
    }

    @Override
    public void setTitleAt(int index, String title) {
        super.setTitleAt(index, title);
        ((TabLabel) getTabComponentAt(index)).getTitleLabel().setText(title);
    }

    public Tab currentTab() {
        return (Tab) getSelectedComponent();
    }

    static class TabLabel extends JPanel {
        private JButton closeButton;
        private JLabel titleLabel;

        public TabLabel(String title) {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            closeButton = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();

                    if (getModel().isPressed())
                        g2.translate(1, 1);

                    g2.setStroke(new Stroke() {
                        BasicStroke stroke1 = new BasicStroke(1),
                                stroke2 = new BasicStroke(1);

                        public Shape createStrokedShape(Shape s) {
                            return stroke2.createStrokedShape(stroke1.createStrokedShape(s));
                        }
                    });

                    g2.setColor(getModel().isRollover() ? Color.RED : new Color(0x090909));

                    int delta = 1;
                    g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                    g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                    g2.dispose();
                }

                {
                    setUI(new BasicButtonUI());
                    setContentAreaFilled(false);
                    setFocusable(false);
                    setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
                }
            };

            setOpaque(false);
            setFocusable(false);

            add(titleLabel = new JLabel(title));
            add(Box.createHorizontalStrut(3));
            add(closeButton);
        }

        public JButton getCloseButton() {
            return closeButton;
        }

        public JLabel getTitleLabel() {
            return titleLabel;
        }
    }
}
