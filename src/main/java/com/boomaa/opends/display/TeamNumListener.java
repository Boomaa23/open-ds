package com.boomaa.opends.display;

import com.boomaa.opends.networking.NetworkReloader;

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
        NetworkReloader.reloadRio();
        NetworkReloader.reloadFms();
    }
}
