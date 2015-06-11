package quantum.browser.natives;

import org.cef.OS;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class to load CEF libraries.
 */
public class NativeLoader {
    /**
     * Get the platform string.
     * @return
     */
    public static String getPlatform() {
        if (OS.isWindows())
            return "win" + System.getProperty("sun.arch.data.model");
        return null;
    }

    /**
     * Read one line from a text file in a jar.
     * @param path
     * @return
     */
    private static String readJarFileLine(String path) {
        InputStream versionStream = NativeLoader.class.getClassLoader().getResourceAsStream(path);
        if (versionStream == null)
            return null;
        try {
            return new BufferedReader(new InputStreamReader(versionStream)).readLine();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get the temporary file path for the binaries, creating if necessary.
     * @param platform
     * @return
     */
    public static File getBinaryPath(String platform) {
        String version = readJarFileLine("org/cef/binaries/" + platform + "/version.txt");
        if (version == null)
            return null;
        String commonVersion = readJarFileLine("org/cef/binaries/" + platform + "/version.txt");
        if (!version.equals(commonVersion)) {
            System.out.println("Malformed common package");
            return null;
        }

        File path = Paths.get(System.getProperty("java.io.tmpdir"), "quantum_browser", "natives", platform + "_" + version).toFile();
        path.mkdirs();
        return path;
    }

    /**
     * Unpack a file from the jar.
     * @param loader
     * @param jarPath
     * @param file
     * @throws IOException
     */
    public static void copy(ClassLoader loader, String jarPath, File file) throws IOException {
        if (file.exists()) return;
        System.out.printf("Unpacking %s -> %s\n", jarPath, file);
        file.getParentFile().mkdirs();
        try {
            Files.copy(loader.getResourceAsStream(jarPath), file.toPath());
        } catch (FileAlreadyExistsException ignored) {}
    }

    /**
     * Unpacks a directory in the jar.
     * @param platform
     * @param directory
     */
    public static void unpack(String platform, File directory) {
        ClassLoader classLoader = NativeLoader.class.getClassLoader();
        String jarPath = "org/cef/binaries/" + platform + "/";
        InputStream filesStream = classLoader.getResourceAsStream(jarPath + "files.txt");
        if (filesStream == null)
            throw new UnsupportedOperationException("Malformed package");
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(filesStream));
            while ((line = reader.readLine()) != null) {
                copy(classLoader, jarPath + line, new File(directory, line.replace('/', File.separatorChar)));
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException("Malformed package");
        }
    }

    /**
     * Unpack all the native binaries.
     */
    public static void unpack() {
        String platform = getPlatform();
        if (platform == null)
            throw new UnsupportedOperationException("Unsupported platform");

        File binaryPath = getBinaryPath(platform);
        if (binaryPath == null)
            throw new UnsupportedOperationException("No binary for platform");

        unpack("common", binaryPath);
        unpack(platform, binaryPath);

        String oldPath = System.getProperty("java.library.path");
        System.setProperty("java.library.path", oldPath == null ? binaryPath.toString() :
                oldPath + File.pathSeparator + binaryPath.toString());

        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
