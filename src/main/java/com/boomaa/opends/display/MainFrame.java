package com.boomaa.opends.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainFrame implements JDEC {
    public static void display() {
        CONTENT.setLayout(new GridBagLayout());
        FMS_BTN.addActionListener((e) -> new FMSWindow());
        LOG_BTN.addActionListener((e) -> new LogWindow());
        TEAM_NUMBER.getDocument().addDocumentListener(new TeamNumListener());

        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setPreferredSize(new Dimension(800, 600));
        FRAME.setResizable(false);
        FRAME.pack();
        FRAME.setLocationRelativeTo(null);
        FRAME.setVisible(true);
        DisplayEndpoint.initServers();
    }

    public static ButtonGroup createButtonGroup(JRadioButton... buttons) {
        ButtonGroup temp = new ButtonGroup();
        for (JRadioButton button : buttons) {
            temp.add(button);
        }
        return temp;
    }

    public static Container addToPanel(Container panel, Component... comps) {
        for (Component comp : comps) {
            panel.add(comp);
        }
        return panel;
    }

    public static void addConstrainedPanel(int x, int y, int width, int height, Component... comps) {
        GBC.gridx = x;
        GBC.gridy = y;
        GBC.gridwidth = width;
        GBC.gridheight = height;
        if (comps.length != 1) {
            FRAME.getContentPane().add(addToPanel(new JPanel(), comps), GBC);
        } else {
            FRAME.getContentPane().add(comps[0], GBC);
        }
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
