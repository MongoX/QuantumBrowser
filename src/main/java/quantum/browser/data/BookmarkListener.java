package quantum.browser.data;

public interface BookmarkListener {
    void addBookmark(String name, String url);
    void editBookmark(String name, String url);
    void removeBookmark(String name);
}
