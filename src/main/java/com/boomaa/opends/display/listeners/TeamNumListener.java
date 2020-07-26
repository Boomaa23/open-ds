package com.boomaa.opends.display.listeners;

import com.boomaa.opends.display.DisplayEndpoint;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TeamNumListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        DisplayEndpoint.initServers();
    }
}
