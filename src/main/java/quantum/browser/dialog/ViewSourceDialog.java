package quantum.browser.dialog;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import quantum.browser.ui.MainFrame;
import quantum.browser.utils.Resources;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;

/**
 * Syntax highlighted view source dialog.
 */
public class ViewSourceDialog extends JDialog implements Runnable {
    public ViewSourceDialog(MainFrame owner, String url, final String content) {
        super(owner, "View Source: " + url, false);
        setLayout(new BorderLayout());
        setSize(new Dimension(640, 480));

        // Set up the library used.
        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setEditable(false);
        textArea.setText(content);
        add(new RTextScrollPane(textArea));
        // Close handling.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void run() {
        setVisible(true);
    }

    /**
     * A main method just to test the dialog.
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        System.out.print("Loading...");
        InputStream source = new URL("http://en.wikipedia.org/wiki/Main_Page").openStream();
        String html = Resources.toString(source);
        System.out.println(" Done");
        System.out.println(html);
        System.out.println(html.length());
        SwingUtilities.invokeLater(new ViewSourceDialog(null, "http://en.wikipedia.org/wiki/Main_Page", html));
    }
}
