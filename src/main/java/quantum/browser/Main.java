package quantum.browser;

import org.cef.OS;
import quantum.browser.natives.NativeLoader;

import javax.swing.*;

public class Main {
    public static void main(String... args) {
        NativeLoader.unpack();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(OS.isLinux()).setVisible(true);
            }
        });
    }
}
