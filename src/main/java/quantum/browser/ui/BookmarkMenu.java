package quantum.browser.ui;

import quantum.browser.data.Bookmarks;

import javax.swing.*;

public class BookmarkMenu extends JMenu {
    private final TabManager manager;
    private final Bookmarks store;

    public BookmarkMenu(TabManager manager, Bookmarks store) {
        this.manager = manager;
        this.store = store;

        add(new BookmarkAdd(manager, store));
        addSeparator();

        for (String child : store.folders())
            add(new BookmarkMenu(manager, store.getFolder(child)));

        for (String bookmark : store.getList())
            add(new Bookmark(manager, bookmark, store));
    }
}
