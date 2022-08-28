package com.boomaa.opends.display.elements;

import com.boomaa.opends.display.NullDocumentEvent;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentListener;

public class HOverlayField extends HideBase<OverlayField> {
    private final List<DocumentListener> listeners = new ArrayList<>();
    private String text;

    public HOverlayField(final String hint, final int col) {
        super(() -> new OverlayField(hint, col));
    }

    public String getText() {
        return getElement() == null ? text : getElement().getText();
    }

    public void setText(String text) {
        if (getElement() != null) {
            getElement().setText(text);
        } else {
            this.text = text;
            for (DocumentListener listener : listeners) {
                listener.changedUpdate(NullDocumentEvent.getInstance());
            }
        }
    }

    public void addDocumentListener(DocumentListener listener) {
        if (getElement() != null) {
            getElement().getDocument().addDocumentListener(listener);
        } else {
            listeners.add(listener);
        }
    }

    public int checkedIntParse() {
        return getElement() == null ? checkedIntParse(-1) : getElement().checkedIntParse();
    }

    public int checkedIntParse(int defRtn) {
        try {
            return Integer.parseInt(this.getText());
        } catch (NumberFormatException ignored) {
        }
        return defRtn;
    }
}
