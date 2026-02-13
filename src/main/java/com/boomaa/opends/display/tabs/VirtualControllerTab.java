package com.boomaa.opends.display.tabs;

import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.usb.Component;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.usb.VirtualController;
import com.boomaa.opends.util.Debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * A tab that provides on-screen arrow keys and gamepad buttons to simulate
 * controller/joystick inputs. The virtual gamepad is registered as a
 * controller device and its axes/buttons are sent to the robot just
 * like a real USB controller.
 *
 * Keyboard bindings:
 *   WASD / Arrow Keys  -> Left stick axes (X, Y)
 *   IJKL              -> Right stick axes (Z, RY)
 *   Q / E             -> Left / Right triggers (RZ, RX)
 *   1-4               -> A, B, X, Y buttons
 *   5-6               -> LB, RB bumpers
 *   7-8               -> Back, Start
 *
 * On-screen buttons mirror the same inputs with click/hold.
 */
public class VirtualControllerTab extends TabBase {
    private VirtualController virtualCtrl;
    private HIDDevice virtualDevice;
    private Map<Integer, Runnable> keyPressActions;
    private Map<Integer, Runnable> keyReleaseActions;

    // Axis state tracking for keyboard (multiple keys can be held)
    private boolean leftUp, leftDown, leftLeft, leftRight;
    private boolean rightUp, rightDown, rightLeft, rightRight;
    private boolean triggerLeft, triggerRight;

    // UI labels for live axis values
    private JLabel lblLX, lblLY, lblRX, lblRY, lblLT, lblRT;

    public VirtualControllerTab() {
        super(new Dimension(520, 275));
    }

    @Override
    public void config() {
        keyPressActions = new HashMap<>();
        keyReleaseActions = new HashMap<>();

        virtualCtrl = new VirtualController();
        virtualDevice = new HIDDevice(virtualCtrl);
        ControlDevices.getAll().put(virtualDevice.getIdx(), virtualDevice);
        JoystickTab.EmbeddedJDEC.LIST_MODEL.addElement(virtualDevice);
        Debug.println("Virtual gamepad controller registered at index " + virtualDevice.getIdx());

        setLayout(new GridBagLayout());
        setFocusable(true);
        GBCPanelBuilder base = new GBCPanelBuilder(this)
            .setFill(GridBagConstraints.BOTH)
            .setAnchor(GridBagConstraints.CENTER)
            .setInsets(new Insets(4, 4, 4, 4));

        // --- Enable checkbox ---
        JCheckBox enableCb = new JCheckBox("Enable Virtual Gamepad", true);
        enableCb.addItemListener(e -> {
            boolean sel = enableCb.isSelected();
            virtualDevice.setDisabled(!sel);
            Debug.println("Virtual gamepad " + (sel ? "enabled" : "disabled"));
        });
        base.clone().setPos(0, 0, 4, 1).setFill(GridBagConstraints.NONE).build(enableCb);

        // --- Left Stick (D-pad / WASD / Arrow Keys) ---
        JPanel leftStickPanel = createStickPanel("Left Stick (WASD / Arrows)",
            Component.Axis.X, Component.Axis.Y, true);
        base.clone().setPos(0, 1, 2, 3).build(leftStickPanel);

        // --- Right Stick (IJKL) ---
        JPanel rightStickPanel = createStickPanel("Right Stick (IJKL)",
            Component.Axis.Z, Component.Axis.RY, false);
        base.clone().setPos(2, 1, 2, 3).build(rightStickPanel);

        // --- Triggers ---
        JPanel triggerPanel = new JPanel(new GridBagLayout());
        triggerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Triggers",
            TitledBorder.CENTER, TitledBorder.TOP));
        GBCPanelBuilder tBase = new GBCPanelBuilder(triggerPanel)
            .setInsets(new Insets(2, 4, 2, 4))
            .setFill(GridBagConstraints.HORIZONTAL);

        JButton ltBtn = makeHoldButton("LT (Q)");
        JButton rtBtn = makeHoldButton("RT (E)");
        lblLT = new JLabel("0.00", SwingConstants.CENTER);
        lblRT = new JLabel("0.00", SwingConstants.CENTER);

        setupHold(ltBtn, () -> { triggerLeft = true; updateTriggers(); },
                          () -> { triggerLeft = false; updateTriggers(); });
        setupHold(rtBtn, () -> { triggerRight = true; updateTriggers(); },
                          () -> { triggerRight = false; updateTriggers(); });

        tBase.clone().setPos(0, 0, 1, 1).build(ltBtn);
        tBase.clone().setPos(1, 0, 1, 1).build(rtBtn);
        tBase.clone().setPos(0, 1, 1, 1).setFill(GridBagConstraints.NONE).build(lblLT);
        tBase.clone().setPos(1, 1, 1, 1).setFill(GridBagConstraints.NONE).build(lblRT);

        base.clone().setPos(0, 4, 2, 1).build(triggerPanel);

        // --- Gamepad Buttons ---
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Buttons (1-8)",
            TitledBorder.CENTER, TitledBorder.TOP));
        GBCPanelBuilder bBase = new GBCPanelBuilder(buttonPanel)
            .setInsets(new Insets(2, 3, 2, 3));

        String[] btnLabels = {"A(1)", "B(2)", "X(3)", "Y(4)", "LB(5)", "RB(6)", "Back(7)", "Start(8)"};
        Color[] btnColors = {
            new Color(0x4CAF50), new Color(0xF44336), new Color(0x2196F3), new Color(0xFFEB3B),
            null, null, null, null
        };
        for (int i = 0; i < btnLabels.length; i++) {
            JButton btn = makeHoldButton(btnLabels[i]);
            if (btnColors[i] != null) {
                btn.setForeground(btnColors[i]);
            }
            final int idx = i;
            setupHold(btn,
                () -> { virtualCtrl.setButton(idx, true); },
                () -> { virtualCtrl.setButton(idx, false); });
            bBase.clone().setPos(i % 4, i / 4, 1, 1).build(btn);
        }

        base.clone().setPos(2, 4, 2, 1).build(buttonPanel);

        // --- Keyboard bindings ---
        setupKeyBindings();

        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                Runnable action = keyPressActions.get(e.getKeyCode());
                if (action != null) {
                    action.run();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Runnable action = keyReleaseActions.get(e.getKeyCode());
                if (action != null) {
                    action.run();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) { }
        });

        // Focus hint
        JLabel hint = new JLabel("Click this tab and use keyboard for input", SwingConstants.CENTER);
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 10f));
        base.clone().setPos(0, 5, 4, 1).setFill(GridBagConstraints.NONE).build(hint);

        Debug.println("VirtualControllerTab configured");
    }

    private JPanel createStickPanel(String title, Component.Axis xAxis, Component.Axis yAxis, boolean isLeft) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title,
            TitledBorder.CENTER, TitledBorder.TOP));

        GBCPanelBuilder gb = new GBCPanelBuilder(panel)
            .setInsets(new Insets(2, 2, 2, 2))
            .setFill(GridBagConstraints.BOTH);

        JButton upBtn = makeHoldButton("\u25B2");
        JButton downBtn = makeHoldButton("\u25BC");
        JButton leftBtn = makeHoldButton("\u25C0");
        JButton rightBtn = makeHoldButton("\u25B6");

        JLabel xLabel = new JLabel("X: 0.00", SwingConstants.CENTER);
        JLabel yLabel = new JLabel("Y: 0.00", SwingConstants.CENTER);

        if (isLeft) {
            lblLX = xLabel;
            lblLY = yLabel;
            setupHold(upBtn,    () -> { leftUp = true; updateLeftStick(); },
                                () -> { leftUp = false; updateLeftStick(); });
            setupHold(downBtn,  () -> { leftDown = true; updateLeftStick(); },
                                () -> { leftDown = false; updateLeftStick(); });
            setupHold(leftBtn,  () -> { leftLeft = true; updateLeftStick(); },
                                () -> { leftLeft = false; updateLeftStick(); });
            setupHold(rightBtn, () -> { leftRight = true; updateLeftStick(); },
                                () -> { leftRight = false; updateLeftStick(); });
        } else {
            lblRX = xLabel;
            lblRY = yLabel;
            setupHold(upBtn,    () -> { rightUp = true; updateRightStick(); },
                                () -> { rightUp = false; updateRightStick(); });
            setupHold(downBtn,  () -> { rightDown = true; updateRightStick(); },
                                () -> { rightDown = false; updateRightStick(); });
            setupHold(leftBtn,  () -> { rightLeft = true; updateRightStick(); },
                                () -> { rightLeft = false; updateRightStick(); });
            setupHold(rightBtn, () -> { rightRight = true; updateRightStick(); },
                                () -> { rightRight = false; updateRightStick(); });
        }

        // Layout: arrow cross + value labels
        //       [up]
        // [left]     [right]
        //      [down]
        //   X: ...   Y: ...
        gb.clone().setPos(1, 0, 1, 1).build(upBtn);
        gb.clone().setPos(0, 1, 1, 1).build(leftBtn);
        gb.clone().setPos(2, 1, 1, 1).build(rightBtn);
        gb.clone().setPos(1, 2, 1, 1).build(downBtn);
        gb.clone().setPos(0, 3, 1, 1).setFill(GridBagConstraints.NONE).build(xLabel);
        gb.clone().setPos(2, 3, 1, 1).setFill(GridBagConstraints.NONE).build(yLabel);

        return panel;
    }

    private void setupKeyBindings() {
        // Left stick: WASD + Arrow keys
        bindKey(KeyEvent.VK_W,     () -> { leftUp = true; updateLeftStick(); },
                                   () -> { leftUp = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_S,     () -> { leftDown = true; updateLeftStick(); },
                                   () -> { leftDown = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_A,     () -> { leftLeft = true; updateLeftStick(); },
                                   () -> { leftLeft = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_D,     () -> { leftRight = true; updateLeftStick(); },
                                   () -> { leftRight = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_UP,    () -> { leftUp = true; updateLeftStick(); },
                                   () -> { leftUp = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_DOWN,  () -> { leftDown = true; updateLeftStick(); },
                                   () -> { leftDown = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_LEFT,  () -> { leftLeft = true; updateLeftStick(); },
                                   () -> { leftLeft = false; updateLeftStick(); });
        bindKey(KeyEvent.VK_RIGHT, () -> { leftRight = true; updateLeftStick(); },
                                   () -> { leftRight = false; updateLeftStick(); });

        // Right stick: IJKL
        bindKey(KeyEvent.VK_I, () -> { rightUp = true; updateRightStick(); },
                               () -> { rightUp = false; updateRightStick(); });
        bindKey(KeyEvent.VK_K, () -> { rightDown = true; updateRightStick(); },
                               () -> { rightDown = false; updateRightStick(); });
        bindKey(KeyEvent.VK_J, () -> { rightLeft = true; updateRightStick(); },
                               () -> { rightLeft = false; updateRightStick(); });
        bindKey(KeyEvent.VK_L, () -> { rightRight = true; updateRightStick(); },
                               () -> { rightRight = false; updateRightStick(); });

        // Triggers: Q (left), E (right)
        bindKey(KeyEvent.VK_Q, () -> { triggerLeft = true; updateTriggers(); },
                               () -> { triggerLeft = false; updateTriggers(); });
        bindKey(KeyEvent.VK_E, () -> { triggerRight = true; updateTriggers(); },
                               () -> { triggerRight = false; updateTriggers(); });

        // Buttons: number keys 1-8
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            bindKey(KeyEvent.VK_1 + i,
                () -> virtualCtrl.setButton(idx, true),
                () -> virtualCtrl.setButton(idx, false));
        }
    }

    private void bindKey(int keyCode, Runnable onPress, Runnable onRelease) {
        keyPressActions.put(keyCode, onPress);
        keyReleaseActions.put(keyCode, onRelease);
    }

    private void updateLeftStick() {
        double x = (leftRight ? 1.0 : 0.0) - (leftLeft ? 1.0 : 0.0);
        double y = (leftDown ? 1.0 : 0.0) - (leftUp ? 1.0 : 0.0);
        virtualCtrl.setAxis(Component.Axis.X, x);
        virtualCtrl.setAxis(Component.Axis.Y, y);
        if (lblLX != null) {
            lblLX.setText(String.format("X: %.2f", x));
        }
        if (lblLY != null) {
            lblLY.setText(String.format("Y: %.2f", y));
        }
    }

    private void updateRightStick() {
        double x = (rightRight ? 1.0 : 0.0) - (rightLeft ? 1.0 : 0.0);
        double y = (rightDown ? 1.0 : 0.0) - (rightUp ? 1.0 : 0.0);
        virtualCtrl.setAxis(Component.Axis.Z, x);
        virtualCtrl.setAxis(Component.Axis.RY, y);
        if (lblRX != null) {
            lblRX.setText(String.format("X: %.2f", x));
        }
        if (lblRY != null) {
            lblRY.setText(String.format("Y: %.2f", y));
        }
    }

    private void updateTriggers() {
        double lt = triggerLeft ? 1.0 : 0.0;
        double rt = triggerRight ? 1.0 : 0.0;
        virtualCtrl.setAxis(Component.Axis.RZ, lt);
        virtualCtrl.setAxis(Component.Axis.RX, rt);
        if (lblLT != null) {
            lblLT.setText(String.format("%.2f", lt));
        }
        if (lblRT != null) {
            lblRT.setText(String.format("%.2f", rt));
        }
    }

    private JButton makeHoldButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setMargin(new Insets(4, 8, 4, 8));
        return btn;
    }

    private void setupHold(JButton btn, Runnable onPress, Runnable onRelease) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                onPress.run();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onRelease.run();
            }
        });
    }
}
