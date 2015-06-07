package quantum.browser.data;

import java.awt.print.Book;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Bookmarks {
    protected Preferences node;

    Bookmarks(Preferences node) {
        this.node = node;
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
