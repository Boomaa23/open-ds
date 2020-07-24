package com.boomaa.opends.display;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.awt.Container;
import java.awt.GridBagConstraints;

public interface JDEC {
     JFrame FRAME = new JFrame("OpenDS");
     GridBagConstraints GBC = new GridBagConstraints();
     Container CONTENT = FRAME.getContentPane();

     JLabel TITLE = new JLabel("OpenDS");
     JLabel LINK = MainFrame.createLinkLabel("github.com/Boomaa23/open-ds");

     JRadioButton ENABLE_BTN = new JRadioButton("Enable");
     JRadioButton DISABLE_BTN = new JRadioButton("Disable");
     ButtonGroup ENABLE_DISABLE_BG = MainFrame.createButtonGroup(ENABLE_BTN, DISABLE_BTN);

     JRadioButton TELEOP_MODE_BTN = new JRadioButton("Teleoperated");
     JRadioButton AUTO_MODE_BTN = new JRadioButton("Autonomous");
     JRadioButton TEST_MODE_BTN = new JRadioButton("Test");
     ButtonGroup ROBOT_MODE_BG = MainFrame.createButtonGroup(TELEOP_MODE_BTN, AUTO_MODE_BTN, TEST_MODE_BTN);

     JRadioButton RED_ALLIANCE_BTN = new JRadioButton("Red");
     JRadioButton BLUE_ALLIANCE_BTN = new JRadioButton("Blue");
     ButtonGroup ALLIANCE_COLOR_BG = MainFrame.createButtonGroup(RED_ALLIANCE_BTN, BLUE_ALLIANCE_BTN);

     JRadioButton ALLIANCE_ONE = new JRadioButton("1");
     JRadioButton ALLIANCE_TWO = new JRadioButton("2");
     JRadioButton ALLIANCE_THREE = new JRadioButton("3");
     ButtonGroup ALLIANCE_NUM_BG = MainFrame.createButtonGroup(ALLIANCE_ONE, ALLIANCE_TWO, ALLIANCE_THREE);

     StickyButton RESTART_CODE_BTN = new StickyButton("Restart Robot Code");
     StickyButton RESTART_ROBO_RIO_BTN = new StickyButton("Restart RoboRIO");
     StickyButton ESTOP_BTN = new StickyButton("Emergency Stop");

     OverlayField GAME_DATA = new OverlayField("Game Data", 10);
     OverlayField TEAM_NUMBER = new OverlayField("Team Number", 10);

     JButton FMS_BTN = new JButton("Simulate FMS");
     JButton LOG_BTN = new JButton("Open Log");

     JLabel BAT_VOLTAGE = new JLabel("0.00 V");
     HideableLabel HAS_CONNECTION = new HideableLabel("Connected", false);
     HideableLabel HAS_BROWNOUT = new HideableLabel("Brownout", false);
     HideableLabel CODE_INITIALIZING = new HideableLabel("Code Initializing", false);
     HideableLabel ROBOT_CODE = new HideableLabel("Robot Code Running", false);
}
