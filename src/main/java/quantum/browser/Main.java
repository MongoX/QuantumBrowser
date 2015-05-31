package quantum.browser;

import org.cef.OS;
import quantum.browser.natives.NativeLoader;
import quantum.browser.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String... args) {
        NativeLoader.unpack();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {}
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(OS.isLinux()).setVisible(true);
            }
        });
    }
}
