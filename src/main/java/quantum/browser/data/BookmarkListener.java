package quantum.browser.data;

public interface BookmarkListener {
    void addedBookmark(String name, String url);
    void editedBookmark(String name, String url);
    void removedBookmark(String name);
}
