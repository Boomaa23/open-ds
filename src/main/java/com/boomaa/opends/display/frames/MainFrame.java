package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.GlobalKeyListener;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.MultiKeyEvent;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.StdRedirect;
import com.boomaa.opends.display.TeamNumListener;
import com.boomaa.opends.display.TeamNumPersist;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.tabs.TabChangeListener;
import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.OperatingSystem;
import com.boomaa.opends.util.Parameter;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainFrame implements MainJDEC {
    public static final Image ICON = Toolkit.getDefaultToolkit()
        .getImage(MainFrame.class.getResource("/icon.png"))
        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
    public static final Image ICON_MIN = Toolkit.getDefaultToolkit()
        .getImage(MainFrame.class.getResource("/icon-min.png"));
    private static final GBCPanelBuilder base = new GBCPanelBuilder(TAB_CONTAINER)
        .setInsets(new Insets(5, 5, 5, 5))
        .setFill(GridBagConstraints.BOTH)
        .setAnchor(GridBagConstraints.CENTER);

    private MainFrame() {
    }

    public static void display() {
        FRAME.setIconImage(MainFrame.ICON);
        if (!FRAME.isHeadless()) {
            FRAME.getContentPane().setLayout(new GridBagLayout());
        }
        TITLE.setText(TITLE.getText() + " " + DisplayEndpoint.CURRENT_VERSION_TAG);

        valueInit();
        layoutInit();
        listenerInit();

        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(true);
        if (OperatingSystem.isWindows()) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
        if (!FRAME.isHeadless()) {
            SwingUtilities.updateComponentTreeUI(FRAME.getElement());
        }
        FRAME.pack();
        FRAME.setLocationRelativeTo(null);
        FRAME.setVisible(true);
        if (!Parameter.DISABLE_HOTKEYS.isPresent()) {
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }
        Debug.println("Display elements initialized and GUI showing");
    }

    private static void valueInit() {
        IS_ENABLED.setEnabled(false);

        String prevTeamNum = TeamNumPersist.load();
        Debug.println("Team number retrieved from file (will set if not empty): " + prevTeamNum);
        if (!prevTeamNum.trim().isEmpty()) {
            TEAM_NUMBER.setText(prevTeamNum);
        }

        Parameter.applyJDECLinks();
    }

    private static void listenerInit() {
        PROTOCOL_YEAR.addActionListener(makeAsyncListener((e) -> {
            DisplayEndpoint.doProtocolUpdate();
            unsetAllInterfaces();
            Debug.println("Protocol year changed to: " + MainJDEC.getProtocolYear());
        }));
        TAB.addChangeListener(TabChangeListener.getInstance());

        USB_CONNECT.addActionListener(makeAsyncListener((e) -> {
            unsetAllInterfaces();
            Debug.println("Connecting to robot over USB");
        }));
        RESTART_CODE_BTN.init();
        RESTART_CODE_BTN.addActionListener((e) -> {
            IS_ENABLED.setSelected(false);
            Debug.println("Restarting robot code");
        });
        ESTOP_BTN.init();
        ESTOP_BTN.addActionListener((e) -> {
            IS_ENABLED.setSelected(false);
            Debug.println("Emergency Stop (ESTOP) initiated");
        });

        // Debug println method checks this, but checking before adding a listener improves performance
        if (Parameter.DEBUG.isPresent()) {
            RESTART_ROBO_RIO_BTN.init();
            RESTART_ROBO_RIO_BTN.addActionListener((e) -> Debug.println("Restarting RoboRIO"));
            IS_ENABLED.addItemListener((e) -> Debug.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "Robot Enabled" : "Robot Disabled"));
            FMS_CONNECT.addItemListener((e) -> Debug.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "FMS connection allowed" : "FMS connection disallowed"));
            USB_CONNECT.addItemListener((e) -> Debug.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "USB connection allowed" : "USB connection disallowed"));
            ROBOT_DRIVE_MODE.addItemListener((e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Debug.println("Drive mode changed to: " + ((RobotMode) e.getItem()).name());
                }
            });
            ALLIANCE_COLOR.addItemListener((e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Debug.println("Alliance color changed to: " + e.getItem());
                }
            });
            ALLIANCE_NUM.addItemListener((e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Debug.println("Alliance number changed to: " + e.getItem());
                }
            });
        }

        TEAM_NUMBER.addDocumentListener(new TeamNumListener());
        Debug.println("Initialized listeners for display elements");

        if (!Parameter.DISABLE_HOTKEYS.isPresent()) {
            StdRedirect.OUT.toNull();
            GlobalScreen.addNativeKeyListener(GlobalKeyListener.INSTANCE
                .addKeyEvent(NativeKeyEvent.VC_ENTER, () -> MainJDEC.IS_ENABLED.setSelected(false))
                .addKeyEvent(NativeKeyEvent.VC_SPACE, MainJDEC.ESTOP_BTN::doClick)
                .addMultiKeyEvent(new MultiKeyEvent(() -> MainJDEC.IS_ENABLED.setSelected(MainJDEC.IS_ENABLED.isEnabled()),
                        NativeKeyEvent.VC_OPEN_BRACKET, NativeKeyEvent.VC_CLOSE_BRACKET, NativeKeyEvent.VC_BACK_SLASH))
            );
            StdRedirect.OUT.reset();
            Debug.println("Registered global hotkey hooks (JNativeHook)");
        }
    }

    private static void layoutInit() {
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

        TAB.addTab("Control", TAB_CONTAINER);
        TAB.addTab("Joysticks", JS_TAB);
        TAB.addTab("Shuffleboard", NT_TAB);
        TAB.addTab("Statistics", STATS_TAB);
        TAB.addTab("Log", LOG_TAB);
        FRAME.add(TAB);
        Debug.println("Swing tabs added to frame");

        Dimension dimension = new Dimension(560, 350);
        FrameBase.applyNonWindowsScaling(dimension);
        FRAME.setPreferredSize(dimension);

        GBCPanelBuilder endr = base.clone().setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE);

        base.clone().setPos(0, 0, 6, 1).setFill(GridBagConstraints.NONE).build(TITLE);
        base.clone().setPos(0, 1, 6, 1).setFill(GridBagConstraints.NONE).build(LINK);
        base.clone().setPos(5, 0, 1, 2).setFill(GridBagConstraints.NONE).build(new JLabel(new ImageIcon(MainFrame.ICON_MIN)));

        base.clone().setPos(0, 2, 1, 1).build(IS_ENABLED);
        base.clone().setPos(1, 2, 1, 1).build(ROBOT_DRIVE_MODE);

        base.clone().setPos(0, 3, 2, 1).setFill(GridBagConstraints.NONE).build(new JLabel("Alliance Station"));
        base.clone().setPos(0, 4, 1, 1).build(ALLIANCE_NUM);
        base.clone().setPos(1, 4, 1, 1).build(ALLIANCE_COLOR);
        endr.clone().setPos(0, 5, 1, 1).build(new JLabel("Team Number:"));
        base.clone().setPos(1, 5, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(TEAM_NUMBER);
        endr.clone().setPos(0, 6, 1, 1).build(new JLabel("Game Data:"));
        base.clone().setPos(1, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(GAME_DATA);
        endr.clone().setPos(2, 6, 1, 1).build(new JLabel("Protocol Year:"));
        base.clone().setPos(3, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(PROTOCOL_YEAR);

        base.clone().setPos(2, 2, 2, 1).build(RESTART_CODE_BTN);
        base.clone().setPos(2, 3, 2, 1).build(RESTART_ROBO_RIO_BTN);
        base.clone().setPos(2, 4, 2, 1).build(ESTOP_BTN);
        base.clone().setPos(2, 5, 1, 1).build(FMS_CONNECT);
        base.clone().setPos(3, 5, 1, 1).build(USB_CONNECT);

        base.clone().setPos(4, 2, 2, 1).setFill(GridBagConstraints.NONE).build(BAT_VOLTAGE);
        endr.clone().setPos(4, 3, 1, 1).build(new JLabel("Robot:"));
        base.clone().setPos(5, 3, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ROBOT_CONNECTION_STATUS);
        endr.clone().setPos(4, 4, 1, 1).build(new JLabel("Code: "));
        base.clone().setPos(5, 4, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ROBOT_CODE_STATUS);
        endr.clone().setPos(4, 5, 1, 1).build(new JLabel("EStop: "));
        base.clone().setPos(5, 5, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(ESTOP_STATUS);
        endr.clone().setPos(4, 6, 1, 1).build(new JLabel("FMS: "));
        base.clone().setPos(5, 6, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(FMS_CONNECTION_STATUS);
        endr.clone().setPos(4, 7, 1, 1).build(new JLabel("Time: "));
        base.clone().setPos(5, 7, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(MATCH_TIME);

        Debug.println("Swing components initialized and ready for display");
    }

    private static void unsetAllInterfaces() {
        DisplayEndpoint.RIO_UDP_CLOCK.restart();
        DisplayEndpoint.RIO_TCP_CLOCK.restart();
        DisplayEndpoint.FMS_UDP_CLOCK.restart();
        DisplayEndpoint.FMS_TCP_CLOCK.restart();
    }

    public static ActionListener makeAsyncListener(Consumer<ActionEvent> func) {
        return (e) -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                func.accept(e);
                return null;
            }
        }.execute();
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
