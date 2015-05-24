package quantum.browser.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabManager extends JTabbedPane {
    private MainFrame owner;
    boolean osrEnabled;

    public TabManager(final MainFrame owner, boolean osrEnabled) {
        this.owner = owner;
        this.osrEnabled = osrEnabled;

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateNavigation(currentTab());
            }
        });
    }

    public void updateNavigation(Tab tab) {
        if (getSelectedComponent() == tab) {
            owner.setTitle(tab.title);
            owner.toolBar.addressBar.setText(tab.browser.getURL());
        }
    }

    public void newTab() {
        Tab tab = new Tab(this, owner.app.createClient());
        insertTab("Loading...", null, tab, null, getSelectedIndex()+1);
        setSelectedComponent(tab);
    }

    public Tab currentTab() {
        return (Tab) getSelectedComponent();
    }
}
