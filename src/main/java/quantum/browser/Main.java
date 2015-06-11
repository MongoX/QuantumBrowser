package quantum.browser;

import org.cef.OS;
import quantum.browser.natives.NativeLoader;
import quantum.browser.ui.MainFrame;

import javax.swing.*;

/**
 * Main class.
 */
public class Main {
    public static void main(String... args) {
        // Unpacks the native libaries.
        NativeLoader.unpack();
        // Set theme to nimbus.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Then metal...
        }
        // Disable light weight popups.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        // Create window.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(OS.isLinux()).setVisible(true);
            }
        });
    }
}
