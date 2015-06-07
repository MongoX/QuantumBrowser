package quantum.browser.ui;

import quantum.browser.data.Bookmarks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class Bookmark extends JMenuItem implements ActionListener {
    private final TabManager manager;
    private String name;
    private Bookmarks folder;

    public Bookmark(TabManager manager, String name, Bookmarks folder) {
        super(name);
        this.manager = manager;
        this.name = name;
        this.folder = folder;
        addActionListener(this);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            final PopupMenu menu = new PopupMenu(name);
            manager.owner.add(menu);
            menu.add(new MenuItem("Delete") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Demolishing!!!!");
                        folder.delete(name);
                        manager.owner.remove(menu);
                    }
                });
            }});
            menu.show(this, e.getX(), e.getY());
        } else
            super.processMouseEvent(e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        manager.currentTab().browser.loadURL(folder.getURL(name));
    }
}
