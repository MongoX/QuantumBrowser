package quantum.browser.ui;

import quantum.browser.data.Bookmarks;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    public void actionPerformed(ActionEvent e) {
        manager.currentTab().browser.loadURL(folder.getURL(name));
    }
}
