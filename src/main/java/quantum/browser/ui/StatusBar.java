package quantum.browser.ui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel statusLabel = new JLabel(" ");
    private JProgressBar loadProgress = new JProgressBar(0, 1000);

    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        loadProgress.setMaximumSize(new Dimension(150, 100));
        add(statusLabel);
        add(Box.createHorizontalGlue());
        add(loadProgress);
    }

    public void setStatus(String message) {
        statusLabel.setText(message == null || message.isEmpty() ? " " : message);
    }

    public void setProgress(int progress) {
        loadProgress.setValue(progress);
    }
}
