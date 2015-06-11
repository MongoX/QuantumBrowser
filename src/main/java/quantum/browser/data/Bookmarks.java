package quantum.browser.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.prefs.*;

/**
 * Bookmark manager. Represents a bookmark folder.
 */
public class Bookmarks {
    // Using the Java preferences system.
    protected Preferences node;
    protected HashSet<String> keys = new HashSet<>();
    protected HashSet<BookmarkListener> bookmarkListeners = new HashSet<>();
    protected HashSet<BookmarkFolderListener> folderListeners = new HashSet<>();

    /**
     * Non-public constructor
     * @param node the preference node used
     */
    Bookmarks(Preferences node) {
        this.node = node;

        try {
            keys.addAll(Arrays.asList(node.keys()));
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        // Convert our own listeners into Java's
        node.addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent e) {
                if (e.getNewValue() != null) {
                    if (keys.add(e.getKey())) {
                        for (BookmarkListener listener : bookmarkListeners)
                            listener.addedBookmark(e.getKey(), e.getNewValue());
                    } else {
                        for (BookmarkListener listener : bookmarkListeners)
                            listener.editedBookmark(e.getKey(), e.getNewValue());
                    }
                } else {
                    keys.remove(e.getKey());
                    for (BookmarkListener listener : bookmarkListeners)
                        listener.removedBookmark(e.getKey());
                }
            }
        });

        node.addNodeChangeListener(new NodeChangeListener() {
            @Override
            public void childAdded(NodeChangeEvent evt) {
                for (BookmarkFolderListener listener : folderListeners)
                    listener.folderAdded(evt.getChild().name());
            }

            @Override
            public void childRemoved(NodeChangeEvent evt) {
                for (BookmarkFolderListener listener : folderListeners)
                    listener.folderRemoved(evt.getChild().name());
            }
        });
    }

    /**
     * Add a bookmark listener.
     * @param listener the listener
     */
    public void addBookmarkListener(BookmarkListener listener) {
        bookmarkListeners.add(listener);
    }

    /**
     * Remove a bookmark listener.
     * @param listener the listener
     */
    public void removeBookmarkListener(BookmarkListener listener) {
        bookmarkListeners.remove(listener);
    }

    /**
     * Add a bookmark folder listener.
     * @param listener the listener
     */
    public void addFolderListener(BookmarkFolderListener listener) {
        folderListeners.add(listener);
    }

    /**
     * Remove a bookmark folder listener.
     * @param listener the listener
     */
    public void removeFolderListener(BookmarkFolderListener listener) {
        folderListeners.remove(listener);
    }

    /**
     * Get all subfolders.
     */
    public String[] folders() {
        try {
            return node.childrenNames();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the subfolder.
     * @param name
     * @return
     */
    public Bookmarks getFolder(String name) {
        try {
            if (node.nodeExists(name))
                return new Bookmarks(node.node(name));
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Create a subfolder.
     * @param name
     * @return the created folder.
     */
    public Bookmarks createFolder(String name) {
        try {
            if (node.nodeExists(name))
                return null;
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        return new Bookmarks(node.node(name));
    }

    /**
     * Get a list of bookmarks
     * @return
     */
    public String[] getList() {
        try {
            return node.keys();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the bookmark's URL.
     * @param name
     * @return
     */
    public String getURL(String name) {
        return node.get(name, null);
    }

    /**
     * Edits the bookmark's URL.
     * @param name
     * @param url
     */
    public void setURL(String name, String url) {
        node.put(name, url);
    }

    /**
     * Flush changes to disk if applicable.
     */
    public void flush() {
        try {
            node.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a bookmark.
     * @param name
     */
    public void delete(String name) {
        node.remove(name);
    }

    /**
     * Delete this folder.
     */
    public void delete() {
        try {
            node.removeNode();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
