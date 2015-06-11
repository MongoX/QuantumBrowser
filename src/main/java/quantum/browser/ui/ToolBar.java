package quantum.browser.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The toolbar,
 */
public class ToolBar extends JToolBar {
    JTextField addressBar = new JTextField();
    private MainFrame owner;
    private TabManager manager;

    /**
     * Helper to create a button on the navigation bar.
     * @param iconName
     * @param toolTip
     * @param listener
     * @return
     */
    private static JButton makeNavButton(final String iconName, final String toolTip, final ActionListener listener) {
        return new JButton() {{
            ImageIcon icon = IconLoader.loadIcon(iconName, 20);
            setIcon(icon);
            setDisabledIcon(IconLoader.disable(icon));
            setToolTipText(toolTip);
            addActionListener(listener);
        }};
    }

    // Create buttons.
    final JButton backButton = makeNavButton("nav-back.png", "Back", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            manager.currentTab().browser.goBack();
        }
    });

    final JButton forwardButton = makeNavButton("nav-forward.png", "Forward", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            manager.currentTab().browser.goForward();
        }
    });

    final JButton refreshButton = makeNavButton("nav-refresh.png", "Refresh", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0)
                manager.currentTab().browser.reload();
            else
                manager.currentTab().browser.reloadIgnoreCache();
        }
    });

    final JButton stopButton = makeNavButton("nav-stop.png", "Stop", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            manager.currentTab().browser.stopLoad();
        }
    });

    /**
     * Constructor.
     * @param owner
     */
    public ToolBar(final MainFrame owner) {
        setLayout(new BorderLayout());
        this.owner = owner;
        manager = owner.tabManager;

        // Layout code. Look at the toolbar to see what this does...

        addressBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                addressBar.selectAll();
            }
        });
        addressBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.currentTab().browser.loadURL(addressBar.getText());
            }
        });

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(backButton);
            add(forwardButton);
            add(refreshButton);
            add(stopButton);
        }}, BorderLayout.WEST);
        add(addressBar, BorderLayout.CENTER);
        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(makeNavButton("nav-tab-new.png", "New Tab", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manager.newTab();
                }
            }));
        }}, BorderLayout.EAST);
    }
}
