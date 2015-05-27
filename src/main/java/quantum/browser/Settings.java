package quantum.browser;

import org.cef.OS;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Settings {
    private static Preferences preferences = Preferences.userNodeForPackage(Settings.class);

    public static Preferences get() {
        return preferences;
    }

    public static String get(String key, String def) {
        return preferences.get(key, def);
    }

    public static boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    public static byte[] getByteArray(String key, byte[] def) {
        return preferences.getByteArray(key, def);
    }

    public static double getDouble(String key, double def) {
        return preferences.getDouble(key, def);
    }

    public static float getFloat(String key, float def) {
        return preferences.getFloat(key, def);
    }

    public static int getInt(String key, int def) {
        return preferences.getInt(key, def);
    }

    public static long getLong(String key, long def) {
        return preferences.getLong(key, def);
    }

    public static void put(String key, String value) {
        preferences.put(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    public static void putByteArray(String key, byte[] value) {
        preferences.putByteArray(key, value);
    }

    public static void putDouble(String key, double value) {
        preferences.putDouble(key, value);
    }

    public static void putFloat(String key, float value) {
        preferences.putFloat(key, value);
    }

    public static void putInt(String key, int value) {
        preferences.putInt(key, value);
    }

    public static void putLong(String key, long value) {
        preferences.putLong(key, value);
    }

    public static Dimension getDimension(String key, Dimension def) {
        String[] value = get(key, "").split(":");
        if (value.length != 2) return def;
        int w, h;
        try {
            w = Integer.parseInt(value[0]);
            h = Integer.parseInt(value[1]);
        } catch (NumberFormatException e) {
            return def;
        }
        return new Dimension(w, h);
    }

    public static void putDimension(String key, Dimension dimension) {
        put(key, String.format("%d:%d", dimension.width, dimension.height));
    }

    public static final String appData, localAppData;
    public static final File dataDirectory, localDataDirectory, cookieDirectory, cacheDirectory;
    public static final File logFile, faviconCache;

    static {
        if (OS.isWindows()) {
            appData = System.getenv("AppData");
        } else if (OS.isMacintosh()) {
            appData = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            appData = System.getProperty("user.home") + "/.local/share";
        }
        localAppData = OS.isWindows() && System.getenv("LocalAppData") != null ? System.getenv("LocalAppData") : appData;
        dataDirectory = Paths.get(appData, "Quantum", "Browser").toFile();
        dataDirectory.mkdirs();
        localDataDirectory = Paths.get(localAppData, "Quantum", "Browser").toFile();
        localDataDirectory.mkdirs();
        cookieDirectory = new File(dataDirectory, "cookies");
        cookieDirectory.mkdir();
        cacheDirectory = new File(localDataDirectory, "cache");
        cacheDirectory.mkdir();
        logFile = new File(localDataDirectory, "debug.log");
        faviconCache = new File(localDataDirectory, "favicon");
        faviconCache.mkdir();
    }
}
