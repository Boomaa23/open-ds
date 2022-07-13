package com.boomaa.opends.display.tabs;

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
    public void stateChanged(ChangeEvent e) {
        if (!(e.getSource() instanceof JTabbedPane)) {
            return;
        }
        JTabbedPane pane = (JTabbedPane) e.getSource();
        TabBase.setVisible((Class<? extends TabBase>) pane.getSelectedComponent().getClass());
        Runnable action = actions.get(pane.getTitleAt(pane.getSelectedIndex()));
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
