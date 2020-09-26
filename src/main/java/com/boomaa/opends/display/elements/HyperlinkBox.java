package com.boomaa.opends.display.elements;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HyperlinkBox extends JEditorPane {
    public HyperlinkBox(String bodyHtml) {
        super("text/html", "<html><body style=\"font-family: Sans-Serif; font-size: 10px; background-color: #f0f0f0;\">" + bodyHtml + "</body></html>");
        addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException exc) {
                    exc.printStackTrace();
                }
            }
        });
        setEditable(false);
        setHighlighter(null);
        setBorder(null);
    }

    public void display(String title) {
        JOptionPane.showMessageDialog(null, this, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
