package quantum.browser.dialog;

import quantum.browser.data.Bookmarks;
import quantum.browser.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookmarkDialog extends JDialog implements Runnable {
    public BookmarkDialog(final MainFrame owner, final Bookmarks store, String defaultURL) {
        super(owner, "Authentication required", true);
        final JTextField name = new JTextField(20);
        final JTextField url = new JTextField(defaultURL, 20);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(new JPanel(new GridLayout(0, 1)) {{
                add(new JLabel("Name: "));
                add(new JLabel("URL: "));
            }});
            add(new JPanel(new GridLayout(0, 1)) {{
                add(name);
                add(url);
            }});
        }});

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(Box.createHorizontalGlue());
            add(new JButton("OK") {{
                BookmarkDialog.this.getRootPane().setDefaultButton(this);
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        store.setURL(name.getText(), url.getText());
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
