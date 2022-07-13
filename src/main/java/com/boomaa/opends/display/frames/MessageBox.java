package com.boomaa.opends.display.frames;

import javax.swing.JOptionPane;

public class MessageBox {
    private MessageBox() {
    }

    public static void show(String message, Type type) {
        JOptionPane.showMessageDialog(null, message, type.title, type.optionFlag);
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
