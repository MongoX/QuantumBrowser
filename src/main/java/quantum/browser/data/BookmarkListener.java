package quantum.browser.data;

/**
 * Interface for bookmark operation notifications.
 */
public interface BookmarkListener {
    /**
     * Called when a bookmark is created.
     * @param name the name of the bookmark
     * @param url the url the bookmark links to
     */
    void addedBookmark(String name, String url);

    /**
     * Called when a bookmark is edited.
     * @param name the name of the bookmark
     * @param url the url the bookmark links to
     */
    void editedBookmark(String name, String url);

    /**
     * Called when a bookmark is deleted.
     * @param name the name of the bookmark
     */
    void removedBookmark(String name);
}
