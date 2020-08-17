package com.boomaa.opends.display.layout;

import com.boomaa.opends.display.TeamNumListener;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.frames.JoystickFrame;
import com.boomaa.opends.display.frames.LogFrame;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.frames.StatsFrame;
import com.boomaa.opends.networking.NetworkReloader;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;

public class Layout2020 extends LayoutPlacer {
    public Layout2020() {
        super(new GBCPanelBuilder(CONTENT).setInsets(new Insets(5, 5, 5, 5)).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER));
    }

    @Override
    public void init() {
        FRAME.setPreferredSize(new Dimension(560, 350));
        LOG_BTN.addActionListener((e) -> new LogFrame());
        JS_BTN.addActionListener((e) -> new JoystickFrame());
        STATS_BTN.addActionListener((e) -> new StatsFrame());
        TEAM_NUMBER.getDocument().addDocumentListener(new TeamNumListener());
        TEAM_NUMBER.setText("5818"); //TODO remove after testing
        IS_ENABLED.setEnabled(false);

        base.clone().setPos(0, 0, 6, 1).setFill(GridBagConstraints.NONE).build(TITLE);
        base.clone().setPos(0, 1, 6, 1).setFill(GridBagConstraints.NONE).build(LINK);
        base.clone().setPos(5, 0, 1, 2).setFill(GridBagConstraints.NONE).build(new JLabel(new ImageIcon(MainFrame.FIRST_LOGO.getScaledInstance(35, 35, Image.SCALE_SMOOTH))));

        base.clone().setPos(0, 2, 1, 1).build(IS_ENABLED);
        base.clone().setPos(1, 2, 1, 1).build(ROBOT_DRIVE_MODE);

        base.clone().setPos(0, 3, 2, 1).setFill(GridBagConstraints.NONE).build(new JLabel("Alliance Station"));
        base.clone().setPos(0, 4, 1, 1).build(ALLIANCE_NUM);
        base.clone().setPos(1, 4, 1, 1).build(ALLIANCE_COLOR);
        base.clone().setPos(0, 5, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Team Number:"));
        base.clone().setPos(1, 5, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(TEAM_NUMBER);
        base.clone().setPos(0, 6, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Game Data:"));
        base.clone().setPos(1, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(GAME_DATA);
        base.clone().setPos(0, 7, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("FMS:"));
        base.clone().setPos(1, 7, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(FMS_TYPE);
        base.clone().setPos(0, 8, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Protocol Year:"));
        base.clone().setPos(1, 8, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(PROTOCOL_YEAR);

        base.clone().setPos(2, 2, 2, 1).build(RESTART_CODE_BTN);
        base.clone().setPos(2, 3, 2, 1).build(RESTART_ROBO_RIO_BTN);
        base.clone().setPos(2, 4, 2, 1).build(ESTOP_BTN);
        base.clone().setPos(2, 5, 2, 1).build(JS_BTN);
        base.clone().setPos(2, 6, 2, 1).build(LOG_BTN);
        base.clone().setPos(2, 7, 2, 1).build(STATS_BTN);
        base.clone().setPos(2, 8, 2, 1).build(SIMULATE_ROBOT);

        base.clone().setPos(4, 2, 2, 1).setFill(GridBagConstraints.NONE).build(BAT_VOLTAGE);
        base.clone().setPos(4, 3, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Robot:"));
        base.clone().setPos(5, 3, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ROBOT_CONNECTION_STATUS);
        base.clone().setPos(4, 4, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Code: "));
        base.clone().setPos(5, 4, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ROBOT_CODE_STATUS);
        base.clone().setPos(4, 5, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("FMS: "));
        base.clone().setPos(5, 5, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(FMS_CONNECTION_STATUS);
        base.clone().setPos(4, 6, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Brownout: "));
        base.clone().setPos(5, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(BROWNOUT_STATUS);
        base.clone().setPos(4, 7, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Time: "));
        base.clone().setPos(5, 7, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(MATCH_TIME);
    }
}