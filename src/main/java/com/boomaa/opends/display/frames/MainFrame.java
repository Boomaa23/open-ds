package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.ProtocolClass;
import com.boomaa.opends.display.layout.LayoutPlacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainFrame implements MainJDEC {
    public static final Image FIRST_LOGO = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/first.png"));

    public static void display() {
        FRAME.setIconImage(FIRST_LOGO);
        CONTENT.setLayout(new GridBagLayout());

        ProtocolClass layout = new ProtocolClass("com.boomaa.opends.display.layout.Layout");
        try {
            ((LayoutPlacer) Class.forName(layout.toString()).getConstructor().newInstance()).init();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(true);
        FRAME.pack();
        FRAME.setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(FRAME);
        FRAME.setVisible(true);
    }

    @Deprecated
    public static ButtonGroup createButtonGroup(JRadioButton... buttons) {
        ButtonGroup temp = new ButtonGroup();
        for (JRadioButton button : buttons) {
            temp.add(button);
        }
        return temp;
    }

    @Deprecated
    public static void setButtonCenteredAbove(JRadioButton... buttons) {
        for (JRadioButton button : buttons) {
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        }
    }

    @Deprecated
    public static void setSelected(JToggleButton... buttons) {
        for (JToggleButton button : buttons) {
            button.setSelected(true);
        }
    }

    public static Container addToPanel(Container panel, Component... comps) {
        for (Component comp : comps) {
            panel.add(comp);
        }
        return panel;
    }

    public static JLabel createLinkLabel(String text) {
        JLabel href = new JLabel("<html><a href=''>" + text + "</a></html>");
        href.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI("https://" + text));
                } catch (IOException | URISyntaxException exc) {
                    exc.printStackTrace();
                }
            }
        });
        return href;
    }
}
