package quantum.browser.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The status bar.
 */
public class StatusBar extends JPanel {
    private JLabel statusLabel = new JLabel(" "); // A space to hold the right height.
    private JProgressBar loadProgress = new JProgressBar(0, 1000);

    /**
     * Constructor.
     */
    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        loadProgress.setMaximumSize(new Dimension(150, 100));
        add(statusLabel);
        add(Box.createHorizontalGlue());
        add(loadProgress);
    }

    /**
     * Update the status.
     * @param message
     */
    public void setStatus(String message) {
        statusLabel.setText(message == null || message.isEmpty() ? " " : message);
    }

    /**
     * Update the progress.
     * @param progress
     */
    public void setProgress(int progress) {
        loadProgress.setValue(progress);
    }
}
