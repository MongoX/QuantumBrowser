package quantum.browser.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Resources {
    public static String readResource(String path) {
        InputStream stream = Resources.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) return null;
        return toString(stream);
    }

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

    public static String toString(InputStream stream) {
        return toString(stream, "UTF-8");
    }

    public static String toString(InputStream stream, String encoding) {
        byte[] bytes = toByteArray(stream);
        if (bytes == null)
            return null;
        return new String(bytes, Charset.forName(encoding));
    }
}
