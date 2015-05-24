package quantum.browser.ui;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar {
    JTextField addressBar = new JTextField();
    private MainFrame owner;
    private TabManager manager;

    public ToolBar(final MainFrame owner) {
        this.owner = owner;
        manager = owner.tabManager;

        addressBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.currentTab().browser.loadURL(addressBar.getText());
            }
        });

        add(addressBar);
        add(new JButton("New Tab") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manager.newTab();
                }
            });
        }});
    }
}
