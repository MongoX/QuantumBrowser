package quantum.browser.ui;

import quantum.browser.data.BookmarkFolderListener;
import quantum.browser.data.BookmarkListener;
import quantum.browser.data.Bookmarks;
import quantum.browser.dialog.BookmarkDialog;
import quantum.browser.dialog.BookmarkFolderDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * A menu to show a folder of bookmarks.
 */
public class BookmarkMenu extends JMenu implements BookmarkListener, BookmarkFolderListener {
    private final TabManager manager;
    private final Bookmarks store;
    private final String name;
    private HashMap<String, Bookmark> bookmarks = new HashMap<>();
    private HashMap<String, BookmarkMenu> folders = new HashMap<>();

    /**
     * Public constructor.
     * @param manager
     * @param store
     */
    public BookmarkMenu(final TabManager manager, final Bookmarks store) {
        this(manager, store, null);
    }

    /**
     * Private constructor to know about the name of the current folder.
     * Only used recursively from this class.
     * @param manager
     * @param store
     * @param name
     */
    private BookmarkMenu(final TabManager manager, final Bookmarks store, String name) {
        super(name);
        this.manager = manager;
        this.store = store;
        this.name = name;

        // Add new item buttons.
        add(new JMenuItem("Add New...") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(new BookmarkDialog(manager.owner, store, manager.currentTab().browser.getURL())).start();
                }
            });
            setMnemonic('N');
        }});
        add(new JMenuItem("Add New Folder...") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(new BookmarkFolderDialog(manager.owner, store, manager.currentTab().browser.getURL())).start();
                }
            });
            setMnemonic('F');
        }});
        addSeparator();

        // Populate.
        for (final String child : store.folders())
            add(new BookmarkMenu(manager, store.getFolder(child), child) {{ folders.put(child, this); }});

        for (final String bookmark : store.getList())
            add(new Bookmark(manager, bookmark, store) {{ bookmarks.put(bookmark, this); }});

        // Listen for updates.
        store.addBookmarkListener(this);
        store.addFolderListener(this);
    }

    @Override
    public void addedBookmark(final String name, String url) {
        // Add new bookmark to the menu.
        add(new Bookmark(manager, name, store) {{ bookmarks.put(name, this); }});
    }

    @Override
    public void editedBookmark(String name, String url) {
        // No visual change.
    }

    @Override
    public void removedBookmark(String name) {
        // Remove the bookmark from the menu.
        remove(bookmarks.remove(name));
    }

    @Override
    public void folderAdded(final String name) {
        // Add the new folder.
        add(new BookmarkMenu(manager, store.getFolder(name), name) {{ folders.put(name, this); }});
    }

    @Override
    public void folderRemoved(String name) {
        // Remove deleted folder.
        remove(folders.remove(name));
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        // Show right click menu.
        if (name != null && SwingUtilities.isRightMouseButton(e)) {
            // Again AWT menu to avoid sketchiness.
            final PopupMenu menu = new PopupMenu(name);
            manager.owner.add(menu);
            menu.add(new MenuItem("Delete") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        store.delete();
                        manager.owner.remove(menu);
                    }
                });
            }});
            menu.show(this, e.getX(), e.getY());
        } else
            super.processMouseEvent(e);
    }
}
