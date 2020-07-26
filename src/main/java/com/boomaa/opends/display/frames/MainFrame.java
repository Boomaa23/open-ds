package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.listeners.FMSTypeListener;
import com.boomaa.opends.display.listeners.SimRobotListener;
import com.boomaa.opends.display.listeners.TeamNumListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainFrame implements MainJDEC {
    public static void display() {
        Image firstLogo = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/first.png"));
        FRAME.setIconImage(firstLogo);
        CONTENT.setLayout(new GridBagLayout());
        LOG_BTN.addActionListener((e) -> new LogFrame());
        TEAM_NUMBER.getDocument().addDocumentListener(new TeamNumListener());
        TEAM_NUMBER.setText("5818"); //TODO remove after testing
        IS_ENABLED.setEnabled(false);
        SIMULATE_ROBOT.addActionListener(new SimRobotListener());
        FMS_TYPE.addItemListener(new FMSTypeListener());

        GBCPanelBuilder base = new GBCPanelBuilder().setInsets(new Insets(5, 5, 5, 5)).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER);

        base.clone().setPos(0, 0, 6, 1).setFill(GridBagConstraints.NONE).build(TITLE);
        base.clone().setPos(0, 1, 6, 1).setFill(GridBagConstraints.NONE).build(LINK);
        base.clone().setPos(5, 0, 1, 2).setFill(GridBagConstraints.NONE).build(new JLabel(new ImageIcon(firstLogo.getScaledInstance(35, 35, Image.SCALE_SMOOTH))));

        base.clone().setPos(0, 2, 1, 1).build(IS_ENABLED);
        base.clone().setPos(1, 2, 1, 1).build(ROBOT_DRIVE_MODE);

        base.clone().setPos(0, 3, 2, 1).setFill(GridBagConstraints.NONE).build(new JLabel("Alliance Station"));
        base.clone().setPos(0, 4, 1, 1).build(ALLIANCE_COLOR);
        base.clone().setPos(1, 4, 1, 1).build(ALLIANCE_NUM);
        base.clone().setPos(0, 5, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Team Number:"));
        base.clone().setPos(1, 5, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(TEAM_NUMBER);
        base.clone().setPos(0, 6, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Game Data:"));
        base.clone().setPos(1, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(GAME_DATA);
        base.clone().setPos(0, 7, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("FMS:"));
        base.clone().setPos(1, 7, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(FMS_TYPE);


        base.clone().setPos(2, 2, 2, 1).build(RESTART_CODE_BTN);
        base.clone().setPos(2, 3, 2, 1).build(RESTART_ROBO_RIO_BTN);
        base.clone().setPos(2, 4, 2, 1).build(ESTOP_BTN);
        base.clone().setPos(2, 5, 2, 1).build(LOG_BTN);
        base.clone().setPos(2, 6, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Protocol Year:"));
        base.clone().setPos(3, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(PROTOCOL_YEAR);
        base.clone().setPos(2, 7, 2, 1).build(SIMULATE_ROBOT);

        base.clone().setPos(4, 2, 2, 1).setFill(GridBagConstraints.NONE).build(BAT_VOLTAGE);
        base.clone().setPos(4, 3, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Robot:"));
        base.clone().setPos(5, 3, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ROBOT_CONNECTION_STATUS);
        base.clone().setPos(4, 4, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Code: "));
        base.clone().setPos(5, 4, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ROBOT_CODE_STATUS);
        base.clone().setPos(4, 5, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("FMS: "));
        base.clone().setPos(5, 5, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(FMS_CONNECTION_STATUS);
        base.clone().setPos(4, 6, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Brownout: "));
        base.clone().setPos(5, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(BROWNOUT_STATUS);

        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setPreferredSize(new Dimension(535, 315));
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

    public static void setButtonCenteredAbove(JRadioButton... buttons) {
        for (JRadioButton button : buttons) {
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        }
    }

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
