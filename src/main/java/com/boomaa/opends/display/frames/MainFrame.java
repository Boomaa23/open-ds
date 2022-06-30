package com.boomaa.opends.display.frames;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.GlobalKeyListener;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.MultiKeyEvent;
import com.boomaa.opends.display.TeamNumListener;
import com.boomaa.opends.display.TeamNumPersist;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.display.tabs.NTTab;
import com.boomaa.opends.display.tabs.TabChangeListener;
import com.boomaa.opends.networking.NetworkReloader;
import com.boomaa.opends.util.OperatingSystem;
import com.boomaa.opends.util.Parameter;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame implements MainJDEC {
    public static final Image ICON = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/icon.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
    public static final Image ICON_MIN = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/icon-min.png"));
    private static final GBCPanelBuilder base = new GBCPanelBuilder(TAB_CONTAINER).setInsets(new Insets(5, 5, 5, 5)).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER);
    public static NTTab NT_FRAME;

    public static void display() {
        FRAME.setIconImage(MainFrame.ICON);
        CONTENT.setLayout(new GridBagLayout());

        TITLE.setText(TITLE.getText() + " " + DisplayEndpoint.CURRENT_VERSION_TAG);

        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        listenerInit();
        valueInit();
        layoutInit();

        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setResizable(true);
        FRAME.pack();
        FRAME.setLocationRelativeTo(null);
        if (OperatingSystem.isWindows()) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
        SwingUtilities.updateComponentTreeUI(FRAME);
        FRAME.setVisible(true);
        if (!Parameter.DISABLE_HOTKEYS.isPresent()) {
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }
    }

    private static void valueInit() {
        IS_ENABLED.setEnabled(false);

        TeamNumPersist.init();
        String prevTeamNum = TeamNumPersist.load();
        if (!prevTeamNum.trim().isEmpty()) {
            TEAM_NUMBER.setText(prevTeamNum);
        }

        Parameter.applyJDECLinks();
    }

    private static void listenerInit() {
        TAB.addChangeListener(TabChangeListener.getInstance());

        USB_CONNECT.addActionListener((e) -> {
            Thread reload = new Thread() {
                @Override
                public void run() {
                    NetworkReloader.reloadRio(Protocol.UDP);
                    NetworkReloader.reloadRio(Protocol.TCP);
                    super.run();
                    interrupt();
                }
            };
            reload.start();
        });
        RESTART_CODE_BTN.addActionListener(e -> IS_ENABLED.setSelected(false));

        TEAM_NUMBER.getDocument().addDocumentListener(new TeamNumListener());

        if (!Parameter.DISABLE_HOTKEYS.isPresent()) {
            GlobalScreen.addNativeKeyListener(GlobalKeyListener.INSTANCE
                    .addKeyEvent(NativeKeyEvent.VC_ENTER, () -> MainJDEC.IS_ENABLED.setSelected(false))
                    .addKeyEvent(NativeKeyEvent.VC_SPACE, MainJDEC.ESTOP_BTN::doClick)
                    .addMultiKeyEvent(new MultiKeyEvent(() -> MainJDEC.IS_ENABLED.setSelected(MainJDEC.IS_ENABLED.isEnabled()),
                            NativeKeyEvent.VC_OPEN_BRACKET, NativeKeyEvent.VC_CLOSE_BRACKET, NativeKeyEvent.VC_BACK_SLASH))
            );
        }
    }

    private static void layoutInit() {
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

        TAB.addTab("Control", TAB_CONTAINER);
        TAB.addTab("Joysticks", JS_TAB);
        TAB.addTab("Shuffleboard", NT_TAB);
        TAB.addTab("Statistics", STATS_TAB);
        TAB.addTab("Log", LOG_TAB);
        FRAME.add(TAB);

        Dimension dimension = new Dimension(560, 350);
        if (OperatingSystem.getCurrent() == OperatingSystem.MACOS) {
            dimension.setSize(dimension.getWidth() * FrameBase.MACOS_WIDTH_SCALE, dimension.getHeight());
        }
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
    }

    @Deprecated
    public static ButtonGroup createButtonGroup(JRadioButton... buttons) {
        ButtonGroup temp = new ButtonGroup();
        for (JRadioButton button : buttons) {
            temp.add(button);
        }
        return temp;
    }

    @Deprecated
    public static void setButtonCenteredAbove(JRadioButton... buttons) {
        for (JRadioButton button : buttons) {
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        }
    }

    @Deprecated
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

    @Deprecated
    public static void createKeyAction(int keyCode, Runnable action) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == keyCode) {
                action.run();
            }
            return false;
        });
    }
}
