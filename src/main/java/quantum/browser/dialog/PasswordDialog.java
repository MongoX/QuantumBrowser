// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package quantum.browser.dialog;

import org.cef.callback.CefAuthCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordDialog extends JDialog implements Runnable {
    private final JTextField username = new JTextField(20);
    private final JPasswordField password = new JPasswordField(20);

    public PasswordDialog(Frame owner, final CefAuthCallback callback) {
        super(owner, "Authentication required", true);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(new JPanel(new GridLayout(0, 1)) {{
                add(new JLabel("Username: "));
                add(new JLabel("Password: "));
            }});
            add(new JPanel(new GridLayout(0, 1)) {{
                add(username);
                add(password);
            }});
        }});

        add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(Box.createHorizontalGlue());
            add(new JButton("OK") {{
                PasswordDialog.this.getRootPane().setDefaultButton(this);
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (username.getText().isEmpty())
                            return;
                        String password = new String(PasswordDialog.this.password.getPassword());
                        callback.Continue(username.getText(), password);
                        setVisible(false);
                        dispose();
                    }
                });
            }});
            add(new JButton("Cancel") {{
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        callback.cancel();
                        setVisible(false);
                        dispose();
                    }
                });
            }});
        }});
        pack();
    }

    @Override
    public void run() {
        setVisible(true);
    }
}
