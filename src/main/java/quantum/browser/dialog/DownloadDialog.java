package quantum.browser.dialog;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;
import quantum.browser.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

public class DownloadDialog extends JDialog implements CefDownloadHandler {
    class DownloadEntry {
        File path = null;
        String filename, url;
        CefDownloadItemCallback callback;
        int id;
        int progress;
        long received, total;
        long speed;
        boolean done = false;

        public DownloadEntry(CefDownloadItem download, String name) {
            filename = name;
            url = download.getURL();
            id = download.getId();
            update(download, null);
        }

        public void update(CefDownloadItem download, CefDownloadItemCallback callback) {
            this.callback = callback;
            if (path == null && !download.getFullPath().isEmpty()) {
                path = new File(download.getFullPath());
                filename = path.getName();
            }
            progress = download.getPercentComplete();
            received = download.getReceivedBytes();
            total = download.getTotalBytes();
            speed = download.getCurrentSpeed();
            done = total == received;
            model.update(this);
        }

        public void abort() {
            if (callback != null) {
                callback.cancel();
                done = true;
            }
        }
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "" + ("kMGTPE").charAt(exp-1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    class DownloadTableModel extends AbstractTableModel {
        private String[] columns = {"File name", "Received", "Size", "Speed", "Progress", ""};
        private Class<?>[] columnClass = {String.class, String.class, String.class, String.class, int.class, DownloadEntry.class};
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
                    return humanReadableByteCount(item.received);
                case 2:
                    return humanReadableByteCount(item.total);
                case 3:
                    return humanReadableByteCount(item.speed) + "/s";
                case 4:
                    return item.progress;
                case 5:
                    return item;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5;
        }
    }

    class ProgressRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressRenderer() {
            setMinimum(0);
            setMaximum(100);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setValue((int) value);
            return this;
        }
    }

    class DownloadButtonRenderer extends JButton implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadEntry entry = (DownloadEntry) value;
            setText(entry.done ? "Remove" : "Abort");
            return this;
        }
    }

    class DownloadButtonEditor extends DefaultCellEditor {
        JButton button;
        DownloadEntry entry;

        public DownloadButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (entry.done) {
                        DownloadDialog.this.model.data.remove(entry);
                        DownloadDialog.this.model.map.remove(entry.id);
                    } else
                        entry.abort();
                    fireEditingStopped();
                    model.fireTableDataChanged();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            entry = (DownloadEntry) value;
            button.setText(entry.done ? "Remove" : "Abort");
            return button;
        }
    }


    protected DownloadTableModel model = new DownloadTableModel();
    protected JTable table = new JTable(model);

    public DownloadDialog(MainFrame owner) {
        super(owner, "Downloads...", false);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setSize(640, 480);
        table.setDefaultRenderer(int.class, new ProgressRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new DownloadButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new DownloadButtonEditor());
    }

    @Override
    public void onBeforeDownload(CefBrowser browser, CefDownloadItem downloadItem, String suggestedName, CefBeforeDownloadCallback callback) {
        DownloadEntry entry = new DownloadEntry(downloadItem, suggestedName);
        model.data.add(entry);
        model.map.put(downloadItem.getId(), entry);
        callback.Continue(suggestedName, true);
    }

    @Override
    public void onDownloadUpdated(CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback) {
        DownloadEntry entry = model.map.get(downloadItem.getId());
        if (entry != null)
            entry.update(downloadItem, callback);
    }
}
