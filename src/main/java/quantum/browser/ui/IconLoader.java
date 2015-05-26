package quantum.browser.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconLoader {
    public static ImageIcon loadIcon(String name) {
        URL icon = IconLoader.class.getClassLoader().getResource("quantum/browser/icons/" + name);
        if (icon == null)
            return null;
        return new ImageIcon(icon);
    }

    public static ImageIcon loadIcon(String name, int height) {
        URL icon = IconLoader.class.getClassLoader().getResource("quantum/browser/icons/" + name);
        if (icon == null)
            return null;
        return new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH));
    }

    public static ImageIcon disable(ImageIcon icon) {
        return new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
    }
}
