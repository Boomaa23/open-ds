package com.boomaa.opends.display;

import com.boomaa.opends.display.elements.HideableLabel;
import com.boomaa.opends.display.elements.KButton;
import com.boomaa.opends.display.elements.MultiValueLabel;
import com.boomaa.opends.display.elements.OverlayField;
import com.boomaa.opends.display.elements.StickyButton;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.display.tabs.LogTab;
import com.boomaa.opends.display.tabs.NTTab;
import com.boomaa.opends.display.tabs.StatsTab;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Container;
import java.awt.GridBagLayout;

// JDEC = (J)ava (D)isplay (E)lement (C)onstants
public interface MainJDEC {
    JFrame FRAME = new JFrame("OpenDS");
    Container CONTENT = FRAME.getContentPane();
    JPanel TAB_CONTAINER = new JPanel(new GridBagLayout());
    JTabbedPane TAB = new JTabbedPane();

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

    JComboBox<Integer> PROTOCOL_YEAR = new JComboBox<>(DisplayEndpoint.VALID_PROTOCOL_YEARS);
    JCheckBox FMS_CONNECT = new JCheckBox("Connect FMS");
    JCheckBox USB_CONNECT = new JCheckBox("Use USB");

    JoystickTab JS_TAB = new JoystickTab();
    NTTab NT_TAB = new NTTab();
    StatsTab STATS_TAB = new StatsTab();
    LogTab LOG_TAB = new LogTab();


    JLabel BAT_VOLTAGE = new JLabel("0.00 V");
    MultiValueLabel ROBOT_CONNECTION_STATUS = new MultiValueLabel(false, "Connected", "Simulated");
    HideableLabel FMS_CONNECTION_STATUS = new HideableLabel(false, "Connected");
    HideableLabel ESTOP_STATUS = new HideableLabel(false, "ESTOP");
    HideableLabel MATCH_TIME = new HideableLabel(false, "0");
    MultiValueLabel ROBOT_CODE_STATUS = new MultiValueLabel(false, "Running", "Initializing");

    JLabel CHALLENGE_RESPONSE = new JLabel("");

    static int getProtocolYear() {
        try {
            return Integer.parseInt(String.valueOf(PROTOCOL_YEAR.getSelectedItem()));
        } catch (NullPointerException | NumberFormatException ignored) {
        }
        return DisplayEndpoint.VALID_PROTOCOL_YEARS[0];
    }

    static int getProtocolIndex() {
        return PROTOCOL_YEAR.getSelectedIndex();
    }
}
