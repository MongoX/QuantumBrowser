package quantum.browser.ui;

import quantum.browser.data.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

/**
 * The menu bar.
 */
public class MenuBar extends JMenuBar {
    /**
     * Create a menu item.
     * @param label
     * @param action
     * @return
     */
    private JMenuItem createMenuItem(String label, final ActionListener action) {
        return new JMenuItem(label) {{
            addActionListener(action);
        }};
    }

    /**
     * Create a menu item with mnemonic.
     * @param label
     * @param mnemonic
     * @param action
     * @return
     */
    private JMenuItem createMenuItem(String label, final char mnemonic, final ActionListener action) {
        return new JMenuItem(label) {{
            setMnemonic(mnemonic);
            addActionListener(action);
        }};
    }

    /**
     * Create a menu item with mnemonic and acclerator.
     * @param label
     * @param mnemonic
     * @param accelerator
     * @param action
     * @return
     */
    private JMenuItem createMenuItem(String label, final char mnemonic, final KeyStroke accelerator, final ActionListener action) {
        return new JMenuItem(label) {{
            setMnemonic(mnemonic);
            setAccelerator(accelerator);
            addActionListener(action);
        }};
    }

    /**
     * Constructor.
     * @param owner owning frame.
     */
    public MenuBar(final MainFrame owner) {
        // Laying out the menu exactly as you see it. Self-explanatory.
        add(new JMenu("File") {{
            setMnemonic('F');
            add(createMenuItem("New Tab", 'N', KeyStroke.getKeyStroke('N', Event.CTRL_MASK), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.newTab();
                }
            }));
            add(createMenuItem("Downloads", 'D', KeyStroke.getKeyStroke('J', Event.CTRL_MASK), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.download.setVisible(true);
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
        add(new BookmarkMenu(owner.tabManager, Settings.bookmarks) {{
            setText("Bookmarks");
            setMnemonic('B');
        }});
        add(new JMenu("Tools") {{
            setMnemonic('T');
            add(createMenuItem("Show Developer Tools", 'F', KeyStroke.getKeyStroke("F12"), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    owner.tabManager.currentTab().showDevTools();
                }
            }));
            add(createMenuItem("Set as Home Page", 'H', new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Settings.put("home_page", owner.tabManager.currentTab().browser.getURL());
                }
            }));
        }});
    }
}
