package quantum.browser.ui;

import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar {
    JTextField addressBar = new JTextField();
    private MainFrame owner;

    public ToolBar(final MainFrame owner) {
        this.owner = owner;

        owner.client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, String url) {
                addressBar.setText(url);
            }
        });

        addressBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                owner.browser.loadURL(addressBar.getText());
            }
        });

        add(addressBar);
    }
}
