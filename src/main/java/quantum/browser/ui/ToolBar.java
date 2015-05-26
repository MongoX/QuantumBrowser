package quantum.browser.ui;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar {
    JTextField addressBar = new JTextField();
    private MainFrame owner;
    private TabManager manager;

    private static JButton makeNavButton(final String iconName, final String toolTip, final ActionListener listener) {
        return new JButton() {{
            ImageIcon icon = IconLoader.loadIcon(iconName, 20);
            setIcon(icon);
            setDisabledIcon(IconLoader.disable(icon));
            setToolTipText(toolTip);
            addActionListener(listener);
        }};
    }

    JButton backButton = makeNavButton("nav-back.png", "Back", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            manager.currentTab().browser.goBack();
        }
    });

    JButton forwardButton = makeNavButton("nav-forward.png", "Forward", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            manager.currentTab().browser.goForward();
        }
    });

    public ToolBar(final MainFrame owner) {
        this.owner = owner;
        manager = owner.tabManager;

        addressBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.currentTab().browser.loadURL(addressBar.getText());
            }
        });

        add(backButton);
        add(forwardButton);
        add(addressBar);
        add(makeNavButton("nav-tab-new.png", "New Tab", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manager.newTab();
                }
            }));
    }
}
