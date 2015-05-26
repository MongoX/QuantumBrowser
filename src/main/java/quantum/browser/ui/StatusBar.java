package quantum.browser.ui;

import javax.swing.*;

public class StatusBar extends JPanel {
    private JLabel statusLabel = new JLabel(" ");

    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(statusLabel);
    }

    public void setStatus(String message) {
        statusLabel.setText(message == null || message.isEmpty() ? " " : message);
    }
}
