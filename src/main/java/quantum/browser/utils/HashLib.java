package quantum.browser.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for FaviconManager. Performs hashing.
 */
public class HashLib {
    private static Charset utf8 = Charset.forName("UTF-8");
    private static String md5Base = new String(new char[32]).replace('\0', '0');

    public static String md5String(String string) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(string.getBytes(utf8));
        String result = new BigInteger(1, hash).toString(16);
        return md5Base.substring(result.length()) + result;
    }
}
