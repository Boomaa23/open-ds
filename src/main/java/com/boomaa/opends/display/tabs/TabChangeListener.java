package com.boomaa.opends.display.tabs;

import com.boomaa.opends.util.Debug;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabChangeListener implements ChangeListener {
    private static final TabChangeListener INSTANCE = new TabChangeListener();
    private static final Map<String, Runnable> actions = new HashMap<>();

    private TabChangeListener() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void stateChanged(ChangeEvent e) {
        if (!(e.getSource() instanceof JTabbedPane)) {
            return;
        }
        JTabbedPane pane = (JTabbedPane) e.getSource();
        TabBase.setVisible((Class<? extends TabBase>) pane.getSelectedComponent().getClass());
        String title = pane.getTitleAt(pane.getSelectedIndex());
        Debug.println("JTab changed to: " + title);
        Runnable action = actions.get(title);
        if (action != null) {
            action.run();
        }
    }

    public TabChangeListener addAction(String tabName, Runnable action) {
        actions.put(tabName, action);
        return this;
    }

    public static TabChangeListener getInstance() {
        return INSTANCE;
    }
}
