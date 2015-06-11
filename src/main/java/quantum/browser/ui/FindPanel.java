package quantum.browser.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel to show the find operation.
 */
public class FindPanel extends JPanel {
    private final Tab tab;
    private JTextField search = new JTextField();
    private JCheckBox caseSensitive = new JCheckBox("Case Sensitive?");

    /**
     * Constructor.
     * @param tab the tab to search on.
     */
    public FindPanel(final Tab tab) {
        this.tab = tab;

        // Layout code. Look at the dialog to see what it does.
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        ActionListener updater = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update(true, false);
            }
        };
        search.addActionListener(updater);
        caseSensitive.addActionListener(updater);

        add(search);
        add(caseSensitive);

        add(new JButton("Previous") {{
            setCancelBinding(this);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FindPanel.this.update(false, true);
                }
            });
        }});
        add(new JButton("Next") {{
            setCancelBinding(this);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FindPanel.this.update(true, true);
                }
            });
        }});
        setCancelBinding(this, search, caseSensitive);
    }

    /**
     * Sketchy method to make all widgets perform exit.
     * @param components
     */
    private void setCancelBinding(JComponent... components) {
        for (JComponent component : components) {
            component.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                        tab.hideFind();
                }
            });
        }
    }

    /**
     * Focuses the search box and select it.
     */
    public void activate() {
        search.requestFocusInWindow();
        search.selectAll();
    }

    /**
     * Update the search results.
     * @param forward direction of search
     * @param followUp first search?
     */
    private void update(boolean forward, boolean followUp) {
        tab.browser.find(0, search.getText(), forward, caseSensitive.isSelected(), followUp);
    }
}
