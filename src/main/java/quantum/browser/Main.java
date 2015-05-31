package quantum.browser;

import org.cef.OS;
import quantum.browser.natives.NativeLoader;
import quantum.browser.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String... args) {
        NativeLoader.unpack();
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(OS.isLinux()).setVisible(true);
            }
        });
    }
}
