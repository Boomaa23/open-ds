package com.boomaa.opends.display.frames;

import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.Parameter;

import javax.swing.JOptionPane;

public class MessageBox {
    private MessageBox() {
    }

    public static void show(String message, Type type) {
        if (Parameter.HEADLESS.isPresent()) {
            JOptionPane.showMessageDialog(null, message, type.title, type.optionFlag);
            Debug.println("Popup message [" + type.name() + "] displayed with content: " + message);
        } else {
            Debug.println("Popup message [" + type.name() + "] was not displayed due to headless mode (with content: " + message + ")");
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
