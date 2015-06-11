package quantum.browser.ui;

import quantum.browser.data.Bookmarks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * A bookmark as an item on the menu.
 */
public class Bookmark extends JMenuItem implements ActionListener {
    private final TabManager manager;
    private String name;
    private Bookmarks folder;

    /**
     * Constructor.
     * @param manager
     * @param name
     * @param folder
     */
    public Bookmark(TabManager manager, String name, Bookmarks folder) {
        super(name);
        this.manager = manager;
        this.name = name;
        this.folder = folder;
        // Listen for updates.
        addActionListener(this);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        // Pops up a menu on right click.
        if (SwingUtilities.isRightMouseButton(e)) {
            // Using AWT menu to avoid sketchy issues.
            final PopupMenu menu = new PopupMenu(name);
            manager.owner.add(menu);
            menu.add(new MenuItem("Delete") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        folder.delete(name);
                        manager.owner.remove(menu);
                    }
                });
            }});
            menu.show(this, e.getX(), e.getY());
        } else
            super.processMouseEvent(e);
    }

    /**
     * Load the page when clicked,
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        manager.currentTab().browser.loadURL(folder.getURL(name));
    }
}
