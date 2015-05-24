package quantum.browser.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar {
    private JMenuItem createMenuItem(String label, final ActionListener action) {
        return new JMenuItem(label) {{
            addActionListener(action);
        }};
    }

    public MenuBar(final MainFrame owner) {
        add(new JMenu("File") {{
            add(createMenuItem("New Tab", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.newTab();
                }
            }));
        }});
    }
}
