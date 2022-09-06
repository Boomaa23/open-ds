package com.boomaa.opends.headless.elements;

import com.boomaa.opends.display.elements.KButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HButton extends HideComponent<KButton> {
    private final List<ActionListener> listeners = new ArrayList<>();
    private final ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "mouseLeftClick");

    public HButton(String label) {
        super(() -> new KButton(label));
    }

    public void addActionListener(ActionListener listener) {
        if (!isHeadless()) {
            getElement().addActionListener(listener);
        } else {
            listeners.add(listener);
        }
    }

    public void doClick() {
        if (!isHeadless()) {
            getElement().doClick();
        } else {
            for (ActionListener l : listeners) {
                l.actionPerformed(event);
            }
        }
    }
}
