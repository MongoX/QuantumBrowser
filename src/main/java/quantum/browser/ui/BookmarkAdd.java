package quantum.browser.ui;

import quantum.browser.data.Bookmarks;
import quantum.browser.dialog.BookmarkDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookmarkAdd extends JMenuItem implements ActionListener {
    private final TabManager manager;
    private final Bookmarks store;

    public BookmarkAdd(TabManager manager, Bookmarks store) {
        super("Add New...");
        this.manager = manager;
        this.store = store;
        addActionListener(this);
        setMnemonic('N');
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new BookmarkDialog(manager.owner, store, manager.currentTab().browser.getURL()).setVisible(true);
    }
}
