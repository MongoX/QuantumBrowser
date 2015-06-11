package quantum.browser.data;

/**
 * Interface for bookmark folder operation notifications.
 */
public interface BookmarkFolderListener {
    /**
     * Called when the folder is created.
     * @param name the name of the folder
     */
    void folderAdded(String name);

    /**
     * Called when the folder is deleted.
     * @param name the name of the folder
     */
    void folderRemoved(String name);
}
