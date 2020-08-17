package com.boomaa.opends.display;

import com.boomaa.opends.display.elements.HideableLabel;
import com.boomaa.opends.display.elements.MultiValueLabel;
import com.boomaa.opends.display.elements.OverlayField;
import com.boomaa.opends.display.elements.StickyButton;
import com.boomaa.opends.display.frames.MainFrame;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Container;

// JDEC = (J)ava (D)isplay (E)lement (C)onstants
public interface MainJDEC {
     JFrame FRAME = new JFrame("OpenDS");
     Container CONTENT = FRAME.getContentPane();

     JLabel TITLE = new JLabel("OpenDS");
     JLabel LINK = MainFrame.createLinkLabel("github.com/Boomaa23/open-ds");

     JCheckBox IS_ENABLED = new JCheckBox("Enable");
     JComboBox<RobotMode> ROBOT_DRIVE_MODE = new JComboBox<>(RobotMode.values());

     JComboBox<String> ALLIANCE_COLOR = new JComboBox<>(new String[] { "Red", "Blue" });
     JComboBox<Integer> ALLIANCE_NUM = new JComboBox<>(new Integer[] { 1, 2, 3 });

     StickyButton RESTART_CODE_BTN = new StickyButton("Restart Robot Code", 10);
     StickyButton RESTART_ROBO_RIO_BTN = new StickyButton("Restart RoboRIO", 10);
     StickyButton ESTOP_BTN = new StickyButton("Emergency Stop", 10);

     OverlayField GAME_DATA = new OverlayField("Game Data", 6);
     OverlayField TEAM_NUMBER = new OverlayField("Team Number", 6);

     JComboBox<Integer> PROTOCOL_YEAR = new JComboBox<>(DisplayEndpoint.getValidProtocolYears());
     JComboBox<FMSType> FMS_TYPE = new JComboBox<>(FMSType.values());

     JCheckBox SIMULATE_ROBOT = new JCheckBox("Simulate Robot");

     JButton LOG_BTN = new JButton("Open Log");
     JButton JS_BTN = new JButton("Joysticks");
     JButton STATS_BTN = new JButton("Statistics");

     JLabel BAT_VOLTAGE = new JLabel("0.00 V");
     HideableLabel ROBOT_CONNECTION_STATUS = new HideableLabel(false, "Connected");
     HideableLabel FMS_CONNECTION_STATUS = new HideableLabel(false, "Connected");
     HideableLabel BROWNOUT_STATUS = new HideableLabel(false, "Voltage");
     HideableLabel MATCH_TIME = new HideableLabel(false, "0");
     MultiValueLabel ROBOT_CODE_STATUS = new MultiValueLabel(false, "Running", "Initializing");

     JLabel CHALLENGE_RESPONSE = new JLabel("");
}
