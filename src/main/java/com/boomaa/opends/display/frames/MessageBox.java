package com.boomaa.opends.display.frames;

import com.boomaa.opends.util.OperatingSystem;

import javax.swing.JOptionPane;

public class MessageBox {
    public static void show(String message, Type type) {
        //TODO linux message boxes
        if (OperatingSystem.isWindows()) {
            JOptionPane.showMessageDialog(null, message, type.title, type.optionFlag);
        }
    }

    public enum Type {
        ERROR(JOptionPane.ERROR_MESSAGE),
        WARNING(JOptionPane.WARNING_MESSAGE),
        INFO(JOptionPane.INFORMATION_MESSAGE),
        QUESTION(JOptionPane.QUESTION_MESSAGE),
        PLAIN(JOptionPane.PLAIN_MESSAGE);

        private final int optionFlag;
        private final String title;

        Type(int optionFlag) {
            this.title = name().charAt(0) + name().toLowerCase().substring(1);
            this.optionFlag = optionFlag;
        }
    }
}
