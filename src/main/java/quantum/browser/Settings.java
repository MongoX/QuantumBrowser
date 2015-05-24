package quantum.browser;

import java.awt.*;
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
}
