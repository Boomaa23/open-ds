package com.boomaa.opends.display;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class NullDocumentEvent implements DocumentEvent {
    private static final NullDocumentEvent INSTANCE = new NullDocumentEvent();

    private NullDocumentEvent() {
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public EventType getType() {
        return null;
    }

    @Override
    public ElementChange getChange(Element elem) {
        return null;
    }

    public static NullDocumentEvent getInstance() {
        return INSTANCE;
    }
}
