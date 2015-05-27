package quantum.browser.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FaviconManager {
    protected Path store;

    public FaviconManager(String store) {
        this.store = Paths.get(store);
        this.store.toFile().mkdirs();
    }

    protected abstract boolean isCustom(String domain);

    protected abstract void setCustom(String domain, boolean custom);

    public ImageIcon getFavicon(String domain, URL favicon) {
        boolean custom = isCustom(domain);
        if (favicon == null && custom)
            return null;
        else if (favicon != null && !custom)
            return null;
        try {
            if (favicon == null)
                favicon = new URL(new URL(domain), "/favicon.ico");
        } catch (MalformedURLException e) {
            return null;
        }
        String hash = HashLib.md5String(favicon.toExternalForm());
        File file = store.resolve(hash + ".png").toFile();
        if (file.exists())
            try {
                return new ImageIcon(file.toURI().toURL());
            } catch (MalformedURLException e) {
                return null;
            }
        else {
            ImageIcon icon = new ImageIcon(favicon);
            Image image = icon.getImage();
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            try {
                ImageIO.write(bufferedImage, "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return icon;
        }
    }
}
