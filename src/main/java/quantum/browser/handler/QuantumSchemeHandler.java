package quantum.browser.handler;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import quantum.browser.utils.Resources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Handles quantum://resources/ requests.
 *
 * Currently unused. Used to be used for view source dialog when it was handled by JS.
 */
public class QuantumSchemeHandler extends CefResourceHandlerAdapter {
    public static final String scheme = "quantum";
    public static final String domain = "resources";
    public static final String prefix = scheme + "://" + domain + "/";

    private byte[] buffer;
    private String mimeType;
    private int offset = 0;

    @Override
    public synchronized boolean processRequest(CefRequest request, CefCallback callback) {
        // Prepares the file and information about it.
        String url = request.getURL();
        if (!url.startsWith(prefix))
            return false;
        String path = url.substring(prefix.length());
        URL resource = QuantumSchemeHandler.class.getClassLoader().getResource("quantum/browser/resources/" + path);
        try {
            mimeType = Files.probeContentType(Paths.get(resource.toURI()));
        } catch (IOException | URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        try {
            buffer = Resources.toByteArray(resource.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        callback.Continue();
        return true;
    }

    @Override
    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        // Provide information about the files.
        response.setMimeType(mimeType);
        response.setStatus(200);
        responseLength.set(buffer.length);
    }

    @Override
    public synchronized boolean readResponse(byte[] output, int toRead, IntRef bytesRead, CefCallback callback) {
        // Actually send the files.
        if (offset < buffer.length) {
            int size = Math.min(toRead, (buffer.length - offset));
            System.arraycopy(buffer, offset, output, 0, size);
            offset += size;
            bytesRead.set(size);
            return true;
        } else {
            offset = 0;
            bytesRead.set(0);
            return false;
        }
    }
}