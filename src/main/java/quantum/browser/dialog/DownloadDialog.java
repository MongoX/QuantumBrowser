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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The download listing window.
 */
public class DownloadDialog extends JDialog implements CefDownloadHandler {
    /**
     * Describes a download.
     */
    class DownloadEntry {
        // Stored data to show in the table.
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

        /**
         * Update download progress and information.
         * @param download
         * @param callback
         */
        public void update(CefDownloadItem download, CefDownloadItemCallback callback) {
            this.callback = callback;
            path = new File(download.getFullPath());
            filename = path.getName();
            progress = download.getPercentComplete();
            received = download.getReceivedBytes();
            total = download.getTotalBytes();
            speed = download.getCurrentSpeed();
            done = total == received;
            model.update(this);
        }

        /**
         * Abortion.
         */
        public void abort() {
            if (callback != null) {
                callback.cancel();
                done = true;
            }
        }
    }

    /**
     * Helper to make large byte numbers readable.
     * @param bytes byte count
     * @return readable string
     */
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "" + ("kMGTPE").charAt(exp-1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Data model for the table.
     */
    class DownloadTableModel extends AbstractTableModel {
        private String[] columns = {"File name", "Received", "Size", "Speed", "Progress", ""};
        private Class<?>[] columnClass = {String.class, String.class, String.class, String.class, int.class, DownloadEntry.class};
        public ArrayList<DownloadEntry> data = new ArrayList<>();
        public HashMap<Integer, DownloadEntry> map = new HashMap<>();

        /**
         * Update the table.
         * @param entry
         */
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
            // Only the buttons are "editable".
            return columnIndex == 5;
        }
    }

    /**
     * Renders a progress bar for downloads.
     */
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

    /**
     * Renders the remove/abort button.
     */
    class DownloadButtonRenderer extends JButton implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadEntry entry = (DownloadEntry) value;
            setText(entry.done ? "Remove" : "Abort");
            return this;
        }
    }

    /**
     * Give life to the remove/abort button.
     *
     * Simply using editing to hack out the pressing.
     */
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

    // Create the table.
    protected DownloadTableModel model = new DownloadTableModel();
    protected JTable table = new JTable(model);

    public DownloadDialog(MainFrame owner) {
        super(owner, "Downloads...", false);

        // Laying out the table.
        add(new JScrollPane(table), BorderLayout.CENTER);
        setSize(640, 480);
        table.setDefaultRenderer(int.class, new ProgressRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(10);
        table.getColumnModel().getColumn(2).setPreferredWidth(10);
        table.getColumnModel().getColumn(3).setPreferredWidth(10);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(30);
        table.getColumnModel().getColumn(5).setCellRenderer(new DownloadButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new DownloadButtonEditor());

        // Double click to open the selected file.
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File path = model.data.get(table.getSelectedColumn()).path;
                    try {
                        Desktop.getDesktop().open(path);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(DownloadDialog.this, "Could not open:\n" + path);
                    }
                }
            }
        });
    }

    /**
     * Stores a download to the table as we get notified.
     * @param browser
     * @param downloadItem
     * @param suggestedName
     * @param callback
     */
    @Override
    public void onBeforeDownload(CefBrowser browser, CefDownloadItem downloadItem, String suggestedName, CefBeforeDownloadCallback callback) {
        setVisible(true);
        DownloadEntry entry = new DownloadEntry(downloadItem, suggestedName);
        model.data.add(entry);
        model.map.put(downloadItem.getId(), entry);
        callback.Continue(suggestedName, true);
    }

    /**
     * Update download information as we get notified.
     * @param browser
     * @param downloadItem
     * @param callback
     */
    @Override
    public void onDownloadUpdated(CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback) {
        DownloadEntry entry = model.map.get(downloadItem.getId());
        if (entry != null)
            entry.update(downloadItem, callback);
    }
}
