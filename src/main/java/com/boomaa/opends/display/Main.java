package com.boomaa.opends.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    private static final JFrame frame = new JFrame("OpenDS");
    private static final GridBagConstraints gbc = new GridBagConstraints();

    public static void main(String[] args) {
        Container content = frame.getContentPane();
        content.setLayout(new GridBagLayout());

        JLabel title = new JLabel("OpenDS");
        JLabel link = createLinkLabel("github.com/Boomaa23/open-ds");

        JRadioButton enableBtn = new JRadioButton("Enable");
        JRadioButton disableBtn = new JRadioButton("Disable");
        ButtonGroup enableDisableBG = createButtonGroup(enableBtn, disableBtn);

        JRadioButton teleopModeBtn = new JRadioButton("Teleoperated");
        JRadioButton autoModeBtn = new JRadioButton("Autonomous");
        JRadioButton testModeBtn = new JRadioButton("Test");
        ButtonGroup robotModeBG = createButtonGroup(teleopModeBtn, autoModeBtn, testModeBtn);

        JRadioButton redAllianceBtn = new JRadioButton("Red");
        JRadioButton blueAllianceBtn = new JRadioButton("Blue");
        ButtonGroup allianceColorBG = createButtonGroup(redAllianceBtn, blueAllianceBtn);

        JRadioButton allianceOne = new JRadioButton("1");
        JRadioButton allianceTwo = new JRadioButton("2");
        JRadioButton allianceThree = new JRadioButton("3");
        ButtonGroup allianceNumBG = createButtonGroup(allianceOne, allianceTwo, allianceThree);

        JButton restartCodeBtn = new JButton("Restart Robot Code");
        JButton restartRoboRioBtn = new JButton("Restart RoboRIO");
        JButton estopBtn = new JButton("Emergency Stop");

        OverlayField gameData = new OverlayField("Game Data", 10);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static ButtonGroup createButtonGroup(JRadioButton... buttons) {
        ButtonGroup temp = new ButtonGroup();
        for (JRadioButton button : buttons) {
            temp.add(button);
        }
        return temp;
    }

    private static Container addToPanel(Container panel, Component... comps) {
        for (Component comp : comps) {
            panel.add(comp);
        }
        return panel;
    }

    private static void addConstrainedPanel(int x, int y, int width, int height, Component... comps) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        if (comps.length != 1) {
            frame.getContentPane().add(addToPanel(new JPanel(), comps), gbc);
        } else {
            frame.getContentPane().add(comps[0], gbc);
        }
    }

    private static JLabel createLinkLabel(String text) {
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
