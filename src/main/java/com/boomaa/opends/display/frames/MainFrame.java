package com.boomaa.opends.display.frames;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.TeamNumListener;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.networking.NetworkReloader;
import com.boomaa.opends.util.OperatingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainFrame implements MainJDEC {
    public static final Image ICON = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/icon.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
    public static final Image ICON_MIN = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/icon-min.png"));
    private static final GBCPanelBuilder base = new GBCPanelBuilder(CONTENT).setInsets(new Insets(5, 5, 5, 5)).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER);
    public static NTFrame NT_FRAME;

    public static void display() {
        FRAME.setIconImage(MainFrame.ICON);
        CONTENT.setLayout(new GridBagLayout());

        TITLE.setText(TITLE.getText() + " " + DisplayEndpoint.CURRENT_VERSION_TAG);

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
    }

    public static void layoutInit() {
        Dimension dimension = new Dimension(560, 320);
        if (OperatingSystem.getCurrent() == OperatingSystem.MACOS) {
            dimension.setSize(dimension.getWidth() * PopupBase.MACOS_WIDTH_SCALE, dimension.getHeight());
        }
        FRAME.setPreferredSize(dimension);
        LOG_BTN.addActionListener((e) -> {
            if (!PopupBase.isAlive(LogFrame.class)) {
                new LogFrame();
            } else {
                PopupBase.getAlive(LogFrame.class).reopen();
            }
        });
        JS_BTN.addActionListener((e) -> {
            MainJDEC.IS_ENABLED.setSelected(false);
            MainJDEC.IS_ENABLED.setEnabled(false);
            if (!PopupBase.isAlive(JoystickFrame.class)) {
                new JoystickFrame();
            } else {
                PopupBase.getAlive(JoystickFrame.class).reopen();
            }
        });
        STATS_BTN.addActionListener((e) -> {
            if (!PopupBase.isAlive(StatsFrame.class)) {
                new StatsFrame();
            } else {
                PopupBase.getAlive(StatsFrame.class).reopen();
            }
        });
        NT_BTN.addActionListener((e) -> {
            if (!PopupBase.isAlive(NTFrame.class)) {
                NT_FRAME = new NTFrame();
            } else {
                PopupBase.getAlive(NTFrame.class).reopen();
            }
        });
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
        TEAM_NUMBER.setText("5818"); //TODO remove after testing
        IS_ENABLED.setEnabled(false);
        createKeyAction(KeyEvent.VK_SPACE, MainJDEC.ESTOP_BTN::doClick);
        createKeyAction(KeyEvent.VK_ENTER, () -> MainJDEC.IS_ENABLED.setSelected(false));
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
        endr.clone().setPos(0, 7, 1, 1).build(new JLabel("Protocol Year:"));
        base.clone().setPos(1, 7, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(PROTOCOL_YEAR);

        base.clone().setPos(2, 2, 2, 1).build(RESTART_CODE_BTN);
        base.clone().setPos(2, 3, 2, 1).build(RESTART_ROBO_RIO_BTN);
        base.clone().setPos(2, 4, 2, 1).build(ESTOP_BTN);
        base.clone().setPos(2, 5, 1, 1).build(JS_BTN);
        base.clone().setPos(3, 5, 1, 1).build(STATS_BTN);
        base.clone().setPos(2, 6, 1, 1).build(NT_BTN);
        base.clone().setPos(3, 6, 1, 1).build(LOG_BTN);
        base.clone().setPos(2, 7, 1, 1).build(FMS_CONNECT);
        base.clone().setPos(3, 7, 1, 1).build(USB_CONNECT);

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

    public static void createKeyAction(int keyCode, Runnable action) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == keyCode) {
                action.run();
            }
            return false;
        });
    }
}
