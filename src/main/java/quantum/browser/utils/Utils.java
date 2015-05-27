package quantum.browser.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    public static String getDomain(String url) {
        try {
            return new URL(new URL(url), "/").toExternalForm();
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
