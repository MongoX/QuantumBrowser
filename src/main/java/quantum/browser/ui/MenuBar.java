package quantum.browser.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class MenuBar extends JMenuBar {
    private JMenuItem createMenuItem(String label, final ActionListener action) {
        return new JMenuItem(label) {{
            addActionListener(action);
        }};
    }

    private JMenuItem createMenuItem(String label, final char mnemonic, final ActionListener action) {
        return new JMenuItem(label) {{
            setMnemonic(mnemonic);
            addActionListener(action);
        }};
    }

    private JMenuItem createMenuItem(String label, final char mnemonic, final KeyStroke accelerator, final ActionListener action) {
        return new JMenuItem(label) {{
            setMnemonic(mnemonic);
            setAccelerator(accelerator);
            addActionListener(action);
        }};
    }

    public MenuBar(final MainFrame owner) {
        add(new JMenu("File") {{
            setMnemonic('F');
            add(createMenuItem("New Tab", 'N', new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.newTab();
                }
            }));
        }});
        add(new JMenu("Edit") {{
            setMnemonic('E');
            add(createMenuItem("Find", 'F', KeyStroke.getKeyStroke('F', Event.CTRL_MASK), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.currentTab().showFind();
                }
            }));
        }});
        add(new JMenu("Tools") {{
            setMnemonic('T');
            add(createMenuItem("Show Developer Tools", 'F', KeyStroke.getKeyStroke("F12"), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.currentTab().showDevTools();
                }
            }));
        }});
    }
}
