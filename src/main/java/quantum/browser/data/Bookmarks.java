package quantum.browser.data;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class Bookmarks {
    protected Preferences node;
    protected HashSet<String> keys = new HashSet<>();
    protected HashSet<BookmarkListener> bookmarkListeners = new HashSet<>();

    Bookmarks(Preferences node) {
        this.node = node;

        try {
            keys.addAll(Arrays.asList(node.keys()));
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        node.addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent e) {
                if (e.getNewValue() != null) {
                    if (keys.add(e.getKey())) {
                        for (BookmarkListener listener : bookmarkListeners)
                            listener.addBookmark(e.getKey(), e.getNewValue());
                    } else {
                        for (BookmarkListener listener : bookmarkListeners)
                            listener.editBookmark(e.getKey(), e.getNewValue());
                    }
                } else {
                    keys.remove(e.getKey());
                    for (BookmarkListener listener : bookmarkListeners)
                        listener.removeBookmark(e.getKey());
                }
            }
        });
    }

    public void addBookmarkListener(BookmarkListener listener) {
        bookmarkListeners.add(listener);
    }

    public void removeBookmarkListener(BookmarkListener listener) {
        bookmarkListeners.remove(listener);
    }

    public String[] folders() {
        try {
            return node.childrenNames();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public Bookmarks getFolder(String name) {
        try {
            if (node.nodeExists(name))
                return new Bookmarks(node.node(name));
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Bookmarks createFolder(String name) {
        try {
            if (node.nodeExists(name))
                return null;
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        return new Bookmarks(node.node(name));
    }

    public String[] getList() {
        try {
            return node.keys();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public String getURL(String name) {
        return node.get(name, null);
    }

    public void setURL(String name, String url) {
        node.put(name, url);
    }

    public void flush() {
        try {
            node.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String name) {
        node.remove(name);
    }

    public void delete() {
        try {
            node.removeNode();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
