package quantum.browser.dialog;

import quantum.browser.data.Bookmarks;
import quantum.browser.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * New bookmark folder dialog.
 */
public class BookmarkFolderDialog extends JDialog implements Runnable {
    public BookmarkFolderDialog(final MainFrame owner, final Bookmarks store, String defaultURL) {
        super(owner, "New Bookmark Folder...", true);
        final JTextField name = new JTextField(20);

        // Layout code. Look at the dialog to see what it does.
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(new JLabel("Name: "));
            add(name);
        }});

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(Box.createHorizontalGlue());
            add(new JButton("OK") {{
                BookmarkFolderDialog.this.getRootPane().setDefaultButton(this);
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        store.createFolder(name.getText());
                        setVisible(false);
                        dispose();
                    }
                });
            }});
            add(new JButton("Cancel") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                        dispose();
                    }
                });
            }});
        }});

        setLocationRelativeTo(owner);
        pack();
    }

    @Override
    public void run() {
        setVisible(true);
    }
}
