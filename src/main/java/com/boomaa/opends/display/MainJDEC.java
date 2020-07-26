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

public interface MainJDEC {
     JFrame FRAME = new JFrame("OpenDS");
     Container CONTENT = FRAME.getContentPane();

     JLabel TITLE = new JLabel("OpenDS");
     JLabel LINK = MainFrame.createLinkLabel("github.com/Boomaa23/open-ds");

     JCheckBox IS_ENABLED = new JCheckBox("Enable");
     JComboBox<RobotMode> ROBOT_DRIVE_MODE = new JComboBox<>(RobotMode.values());

     JComboBox<String> ALLIANCE_COLOR = new JComboBox<>(new String[] { "Red", "Blue" });
     JComboBox<Integer> ALLIANCE_NUM = new JComboBox<>(new Integer[] { 1, 2, 3 });

     StickyButton RESTART_CODE_BTN = new StickyButton("Restart Robot Code");
     StickyButton RESTART_ROBO_RIO_BTN = new StickyButton("Restart RoboRIO");
     StickyButton ESTOP_BTN = new StickyButton("Emergency Stop");

     OverlayField GAME_DATA = new OverlayField("Game Data", 6);
     OverlayField TEAM_NUMBER = new OverlayField("Team Number", 6);

     JComboBox<Integer> PROTOCOL_YEAR = new JComboBox<>(DisplayEndpoint.getValidProtocolYears());
     JComboBox<FMSType> FMS_TYPE = new JComboBox<>(FMSType.values());

     JCheckBox SIMULATE_ROBOT = new JCheckBox("Simulate Robot");

     JButton LOG_BTN = new JButton("Open Log");

     JLabel BAT_VOLTAGE = new JLabel("0.00 V");
     HideableLabel ROBOT_CONNECTION_STATUS = new HideableLabel("Connected", false);
     HideableLabel FMS_CONNECTION_STATUS = new HideableLabel("Connected", true);
     HideableLabel BROWNOUT_STATUS = new HideableLabel("Voltage", true);
     MultiValueLabel ROBOT_CODE_STATUS = new MultiValueLabel(true, "Running", "Initializing");
}
