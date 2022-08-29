package com.boomaa.opends.display;

import com.boomaa.opends.display.elements.HideableLabel;
import com.boomaa.opends.display.elements.MultiValueLabel;
import com.boomaa.opends.display.elements.StickyButton;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.display.tabs.LogTab;
import com.boomaa.opends.display.tabs.NTTab;
import com.boomaa.opends.display.tabs.StatsTab;
import com.boomaa.opends.headless.HCheckBox;
import com.boomaa.opends.headless.HComboBox;
import com.boomaa.opends.headless.HFrame;
import com.boomaa.opends.headless.HOverlayField;

import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

// JDEC = (J)ava (D)isplay (E)lement (C)onstants
public interface MainJDEC {
    HFrame FRAME = new HFrame("OpenDS");
    JPanel TAB_CONTAINER = new JPanel(new GridBagLayout());
    JTabbedPane TAB = new JTabbedPane();

    JLabel TITLE = new JLabel("OpenDS");
    JLabel LINK = MainFrame.createLinkLabel("github.com/Boomaa23/open-ds");

    HCheckBox IS_ENABLED = new HCheckBox("Enable");
    JComboBox<RobotMode> ROBOT_DRIVE_MODE = new JComboBox<>(RobotMode.values());

    HComboBox<String> ALLIANCE_COLOR = new HComboBox<>("Red", "Blue");
    HComboBox<Integer> ALLIANCE_NUM = new HComboBox<>(1, 2, 3);

    StickyButton RESTART_CODE_BTN = new StickyButton("Restart Robot Code", 10);
    StickyButton RESTART_ROBO_RIO_BTN = new StickyButton("Restart RoboRIO", 10);
    StickyButton ESTOP_BTN = new StickyButton("Emergency Stop", 10);

    HOverlayField GAME_DATA = new HOverlayField("Game Data", 6);
    HOverlayField TEAM_NUMBER = new HOverlayField("Team Number", 6);

    HComboBox<Integer> PROTOCOL_YEAR = new HComboBox<>(DisplayEndpoint.VALID_PROTOCOL_YEARS);
    HCheckBox FMS_CONNECT = new HCheckBox("Connect FMS");
    HCheckBox USB_CONNECT = new HCheckBox("Use USB");

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
