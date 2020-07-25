package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.JDEC;
import com.boomaa.opends.display.TeamNumListener;

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
        FMS_BTN.addActionListener((e) -> new FMSFrame());
        LOG_BTN.addActionListener((e) -> new LogFrame());
        TEAM_NUMBER.getDocument().addDocumentListener(new TeamNumListener());
        PROTOCOL_YEAR.setSelectedIndex(0);
        DISABLE_BTN.setSelected(true);
        TELEOP_MODE_BTN.setSelected(true);
        RED_ALLIANCE_BTN.setSelected(true);
        ALLIANCE_ONE.setSelected(true);
        TEAM_NUMBER.setText("5818"); //TODO remove after testing

        //TODO fix gridbaglayout col/row widths
        GBC.anchor = GridBagConstraints.CENTER;
        GBC.fill = GridBagConstraints.NONE;
        GBC.insets = new Insets(5, 5, 5, 5);

        setButtonCenteredAbove(ENABLE_BTN, DISABLE_BTN, TELEOP_MODE_BTN, AUTO_MODE_BTN, TEST_MODE_BTN,
                RED_ALLIANCE_BTN, BLUE_ALLIANCE_BTN, ALLIANCE_ONE, ALLIANCE_TWO, ALLIANCE_THREE);

        addConstrainedPanel(0, 0, 3, 1, ENABLE_BTN);
        addConstrainedPanel(3, 0, 3, 1, DISABLE_BTN);
        addConstrainedPanel(0, 1, 2, 1, TELEOP_MODE_BTN);
        addConstrainedPanel(2, 1, 2, 1, AUTO_MODE_BTN);
        addConstrainedPanel(4, 1, 2, 1, TEST_MODE_BTN);
        addConstrainedPanel(0, 2, 3, 1, LOG_BTN);
        addConstrainedPanel(3, 2, 3, 1, FMS_BTN);

        addConstrainedPanel(0, 3, 6, 1, new JLabel("Alliance Station"));
        addConstrainedPanel(0, 4, 3, 1, RED_ALLIANCE_BTN);
        addConstrainedPanel(3, 4, 3, 1, BLUE_ALLIANCE_BTN);
        addConstrainedPanel(0, 5, 2, 1, ALLIANCE_ONE);
        addConstrainedPanel(2, 5, 2, 1, ALLIANCE_TWO);
        addConstrainedPanel(4, 5, 2, 1, ALLIANCE_THREE);

        addConstrainedPanel(6, 0, 6, 1, BAT_VOLTAGE);
        addConstrainedPanel(6, 1, 6, 1, RESTART_CODE_BTN);
        addConstrainedPanel(6, 2, 6, 1, RESTART_ROBO_RIO_BTN);
        addConstrainedPanel(6, 3, 6, 1, ESTOP_BTN);
        addConstrainedPanel(6, 4, 6, 1, new JLabel("Team Number"));
        addConstrainedPanel(6, 5, 6, 1, TEAM_NUMBER);
        addConstrainedPanel(6, 6, 6, 1, new JLabel("Game Data"));
        addConstrainedPanel(6, 7, 6, 1, GAME_DATA);

        addConstrainedPanel(12, 0, 6, 1, HAS_ROBOT_CONNECTION);
        addConstrainedPanel(12, 1, 6, 1, ROBOT_CODE);
        addConstrainedPanel(12, 2, 6, 1, CODE_INITIALIZING);
        addConstrainedPanel(12, 3, 6, 1, HAS_FMS_CONNECTION);
        addConstrainedPanel(12, 4, 6, 1, HAS_BROWNOUT);
        addConstrainedPanel(12, 5, 6, 1, new JLabel("Protocol Year"));
        addConstrainedPanel(12, 6, 6, 1, PROTOCOL_YEAR);
        addConstrainedPanel(12, 7, 6, 1, CONNECT_REAL_FMS);

        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setPreferredSize(new Dimension(800, 600));
        FRAME.setResizable(true);
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

    public static void setButtonCenteredAbove(JRadioButton... buttons) {
        for (JRadioButton button : buttons) {
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        }
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
