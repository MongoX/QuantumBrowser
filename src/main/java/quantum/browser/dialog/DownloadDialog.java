package quantum.browser.dialog;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;
import quantum.browser.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloadDialog extends JDialog implements CefDownloadHandler {
    class DownloadEntry {
        String filename;
        String url;
        CefDownloadItem download;
        CefDownloadItemCallback callback;
        int id;
        long size;
        long speed;

        public DownloadEntry(CefDownloadItem downloadItem) {
            filename = downloadItem.getSuggestedFileName();
            url = downloadItem.getURL();
            download = downloadItem;
            id = downloadItem.getId();
        }

        public void update(CefDownloadItemCallback callback) {
            this.callback = callback;
            size = download.getTotalBytes();
            speed = download.getCurrentSpeed();
            model.update(this);
        }
    }

    class DownloadTableModel extends AbstractTableModel {
        private String[] columns = {"File name", "Size", "Speed", ""};
        private Class<?>[] columnClass = {String.class, long.class, long.class, DownloadEntry.class};
        public ArrayList<DownloadEntry> data = new ArrayList<>();
        public HashMap<Integer, DownloadEntry> map = new HashMap<>();

        public void update(DownloadEntry entry) {
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columns[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClass[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DownloadEntry item = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return item.filename;
                case 1:
                    return item.size;
                case 2:
                    return item.speed;
                case 3:
                    return item;
            }
            return null;
        }
    }

    protected DownloadTableModel model = new DownloadTableModel();
    protected JTable table = new JTable(model);

    public DownloadDialog(MainFrame owner) {
        super(owner, "Downloads...", false);
        add(table, BorderLayout.CENTER);
        setSize(640, 480);
        pack();
    }

    @Override
    public void onBeforeDownload(CefBrowser browser, CefDownloadItem downloadItem, String suggestedName, CefBeforeDownloadCallback callback) {
        DownloadEntry entry = new DownloadEntry(downloadItem);
        model.data.add(entry);
        model.map.put(downloadItem.getId(), entry);
        callback.Continue(suggestedName, true);
    }

    @Override
    public void onDownloadUpdated(CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback) {
        DownloadEntry entry = model.map.get(downloadItem.getId());
        if (entry != null)
            entry.update(callback);
    }
}
