package quantum.browser.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Resource loader.
 */
public class Resources {
    /**
     * Reads the resource as a string.
     * @param path
     * @return
     */
    public static String readResource(String path) {
        InputStream stream = Resources.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) return null;
        return toString(stream);
    }

    /**
     * Converts InputStream to byte[].
     * @param stream
     * @return
     */
    public static byte[] toByteArray(InputStream stream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[65536];
        int read;
        try {
            while ((read = stream.read(buf)) >= 0)
                baos.write(buf, 0, read);
        } catch (IOException e) {
            return null;
        }
        return baos.toByteArray();
    }

    /**
     * Convert InputStream to String as UTF-8.
     * @param stream
     * @return
     */
    public static String toString(InputStream stream) {
        return toString(stream, "UTF-8");
    }

    /**
     * Convert InputStream to String as any charset.
     * @param stream
     * @param encoding
     * @return
     */
    public static String toString(InputStream stream, String encoding) {
        byte[] bytes = toByteArray(stream);
        if (bytes == null)
            return null;
        return new String(bytes, Charset.forName(encoding));
    }
}
