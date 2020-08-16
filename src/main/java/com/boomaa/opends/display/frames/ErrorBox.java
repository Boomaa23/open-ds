package com.boomaa.opends.display.frames;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorBox {
    public static void show(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
