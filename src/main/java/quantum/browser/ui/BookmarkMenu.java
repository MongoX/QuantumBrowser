package quantum.browser.ui;

import quantum.browser.data.BookmarkListener;
import quantum.browser.data.Bookmarks;

import javax.swing.*;
import java.util.HashMap;

public class BookmarkMenu extends JMenu implements BookmarkListener {
    private final TabManager manager;
    private final Bookmarks store;
    private HashMap<String, Bookmark> bookmarks = new HashMap<>();

    public BookmarkMenu(TabManager manager, Bookmarks store) {
        this.manager = manager;
        this.store = store;

        add(new BookmarkAdd(manager, store));
        addSeparator();

        for (String child : store.folders())
            add(new BookmarkMenu(manager, store.getFolder(child)));

        for (final String bookmark : store.getList())
            add(new Bookmark(manager, bookmark, store) {{ bookmarks.put(bookmark, this); }});

        store.addBookmarkListener(this);
    }

    @Override
    public void addBookmark(final String name, String url) {
        add(new Bookmark(manager, name, store) {{ bookmarks.put(name, this); }});
    }

    @Override
    public void editBookmark(String name, String url) {

    }

    @Override
    public void removeBookmark(String name) {
        remove(bookmarks.remove(name));
    }
}
