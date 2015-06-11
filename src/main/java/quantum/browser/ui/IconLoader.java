package quantum.browser.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Helpers to load icons.
 */
public class IconLoader {
    /**
     * Load an icon.
     * @param name
     * @return
     */
    public static ImageIcon loadIcon(String name) {
        URL icon = IconLoader.class.getClassLoader().getResource("quantum/browser/icons/" + name);
        if (icon == null)
            return null;
        return new ImageIcon(icon);
    }

    /**
     * Load a scaled icon.
     * @param name
     * @param height
     * @return
     */
    public static ImageIcon loadIcon(String name, int height) {
        URL icon = IconLoader.class.getClassLoader().getResource("quantum/browser/icons/" + name);
        if (icon == null)
            return null;
        return new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH));
    }

    /**
     * Make an icon look disabled.
     * @param icon
     * @return
     */
    public static ImageIcon disable(ImageIcon icon) {
        return new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
    }
}
