package quantum.browser.data;

public interface BookmarkFolderListener {
    void folderAdded(String name);
    void folderRemoved(String name);
}
