package quantum.browser.ui;

import quantum.browser.data.BookmarkFolderListener;
import quantum.browser.data.BookmarkListener;
import quantum.browser.data.Bookmarks;
import quantum.browser.dialog.BookmarkDialog;
import quantum.browser.dialog.BookmarkFolderDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class BookmarkMenu extends JMenu implements BookmarkListener, BookmarkFolderListener {
    private final TabManager manager;
    private final Bookmarks store;
    private HashMap<String, Bookmark> bookmarks = new HashMap<>();
    private HashMap<String, BookmarkMenu> folders = new HashMap<>();

    public BookmarkMenu(final TabManager manager, final Bookmarks store) {
        this(manager, store, null);
    }

    private BookmarkMenu(final TabManager manager, final Bookmarks store, String name) {
        super(name);
        this.manager = manager;
        this.store = store;

        add(new JMenuItem("Add New...") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new BookmarkDialog(manager.owner, store, manager.currentTab().browser.getURL()).setVisible(true);
                }
            });
            setMnemonic('N');
        }});
        add(new JMenuItem("Add New Folder...") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new BookmarkFolderDialog(manager.owner, store, manager.currentTab().browser.getURL()).setVisible(true);
                }
            });
            setMnemonic('F');
        }});
        addSeparator();

        for (final String child : store.folders())
            add(new BookmarkMenu(manager, store.getFolder(child), child) {{ folders.put(child, this); }});

        for (final String bookmark : store.getList())
            add(new Bookmark(manager, bookmark, store) {{ bookmarks.put(bookmark, this); }});

        store.addBookmarkListener(this);
        store.addFolderListener(this);
    }

    @Override
    public void addedBookmark(final String name, String url) {
        add(new Bookmark(manager, name, store) {{ bookmarks.put(name, this); }});
    }

    @Override
    public void editedBookmark(String name, String url) {

    }

    @Override
    public void removedBookmark(String name) {
        remove(bookmarks.remove(name));
    }

    @Override
    public void folderAdded(final String name) {
        add(new BookmarkMenu(manager, store.getFolder(name), name) {{ folders.put(name, this); }});
    }

    @Override
    public void folderRemoved(String name) {
        remove(folders.remove(name));
    }
}
