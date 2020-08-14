package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.elements.StickyButton;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.usb.Joystick;
import com.boomaa.opends.usb.USBInterface;
import com.boomaa.opends.usb.XboxController;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.NumberUtils;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class JoystickFrame extends PopupBase {
    private ValUpdater valUpdater;

    public JoystickFrame() {
        super("Joysticks", new Dimension(450, 210));
    }

    @Override
    public void config() {
        super.config();
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        EmbeddedJDEC.LIST.setVisibleRowCount(-1);
        EmbeddedJDEC.LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EmbeddedJDEC.LIST.setLayoutOrientation(JList.VERTICAL);
        EmbeddedJDEC.LIST_SCR.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setPreferredSize(new Dimension(200, 100));
        refreshControllerDisplay();

        EmbeddedJDEC.UP_BTN.addActionListener(e -> {
            int sel = EmbeddedJDEC.LIST.getSelectedIndex();
            if (sel - 1 >= 0) {
                swap(sel, sel - 1);
                EmbeddedJDEC.LIST.setSelectedIndex(sel - 1);
            }
        });
        EmbeddedJDEC.DOWN_BTN.addActionListener(e -> {
            int sel = EmbeddedJDEC.LIST.getSelectedIndex();
            if (sel != -1 && sel + 1 < EmbeddedJDEC.LIST_MODEL.size()) {
                swap(sel, sel + 1);
                EmbeddedJDEC.LIST.setSelectedIndex(sel + 1);
            }
        });
        EmbeddedJDEC.RELOAD_BTN.addActionListener(e -> refreshControllerDisplay());
        EmbeddedJDEC.CLOSE_BTN.addActionListener(e -> this.dispose());

        content.setLayout(new GridBagLayout());
        GBCPanelBuilder base = new GBCPanelBuilder(content).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(new Insets(5, 5, 5, 5));
        GBCPanelBuilder end = base.clone().setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END);

        base.clone().setPos(0, 0, 1, 1).build(EmbeddedJDEC.UP_BTN);
        base.clone().setPos(0, 1, 1, 1).build(EmbeddedJDEC.DOWN_BTN);
        base.clone().setPos(0, 2, 1, 1).build(EmbeddedJDEC.RELOAD_BTN);

        base.clone().setPos(1, 0, 2, 3).build(EmbeddedJDEC.LIST_SCR);

        end.clone().setPos(3, 0, 1, 1).build(new JLabel("X: "));
        end.clone().setPos(3, 1, 1, 1).build(new JLabel("Y: "));
        end.clone().setPos(3, 2, 1, 1).build(new JLabel("Z: "));
        end.clone().setPos(5, 0, 1, 1).build(new JLabel("RX: "));
        end.clone().setPos(5, 1, 1, 1).build(new JLabel("RY: "));

        base.clone().setPos(4, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_X);
        base.clone().setPos(4, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Y);
        base.clone().setPos(4, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Z);
        base.clone().setPos(6, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RX);
        base.clone().setPos(6, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RY);

        base.clone().setPos(0, 3, 7, 1).build(EmbeddedJDEC.CLOSE_BTN);

        if (valUpdater == null || !valUpdater.isAlive()) {
            valUpdater = new ValUpdater();
        }
        valUpdater.start();
    }

    private void swap(int a, int b) {
        HIDDevice aStr = EmbeddedJDEC.LIST_MODEL.getElementAt(a);
        HIDDevice bStr = EmbeddedJDEC.LIST_MODEL.getElementAt(b);
        EmbeddedJDEC.LIST_MODEL.set(a, bStr);
        EmbeddedJDEC.LIST_MODEL.set(b, aStr);
    }

    private void refreshControllerDisplay() {
        EmbeddedJDEC.LIST_MODEL.clear();
        for (HIDDevice js : USBInterface.getControlDevices()) {
            EmbeddedJDEC.LIST_MODEL.add(EmbeddedJDEC.LIST_MODEL.size(), js);
        }
    }

    @Override
    public void dispose() {
        if (valUpdater != null) {
            valUpdater.interrupt();
        }
        super.dispose();
    }

    public interface EmbeddedJDEC {
        DefaultListModel<HIDDevice> LIST_MODEL = new DefaultListModel<>();
        JList<HIDDevice> LIST = new JList<>(LIST_MODEL);
        JScrollPane LIST_SCR = new JScrollPane(LIST);

        JLabel VAL_X = new JLabel();
        JLabel VAL_Y = new JLabel();
        JLabel VAL_Z = new JLabel();
        JLabel VAL_RX = new JLabel();
        JLabel VAL_RY = new JLabel();

        JButton UP_BTN = new JButton("▲");
        JButton DOWN_BTN = new JButton("▼");
        JButton RELOAD_BTN = new JButton("↻");
        StickyButton CLOSE_BTN = new StickyButton("Close", 1);
    }

    public static class ValUpdater extends Clock {
        public ValUpdater() {
            super(20);
        }

        @Override
        public void onCycle() {
            HIDDevice current = EmbeddedJDEC.LIST.getSelectedValue();
            if (current != null) {
                if (current instanceof Joystick) {
                    Joystick js = (Joystick) current;
                    EmbeddedJDEC.VAL_X.setText(String.valueOf(NumberUtils.roundTo(js.getX(), 2)));
                    EmbeddedJDEC.VAL_Y.setText(String.valueOf(NumberUtils.roundTo(js.getY(), 2)));
                    EmbeddedJDEC.VAL_Z.setText(String.valueOf(NumberUtils.roundTo(js.getZ(), 2)));
                    EmbeddedJDEC.VAL_RX.setText(" N/A");
                    EmbeddedJDEC.VAL_RY.setText(" N/A");
                } else if (current instanceof XboxController) {
                    XboxController xbox = (XboxController) current;
                    EmbeddedJDEC.VAL_X.setText(String.valueOf(NumberUtils.roundTo(xbox.getX(true), 2)));
                    EmbeddedJDEC.VAL_Y.setText(String.valueOf(NumberUtils.roundTo(xbox.getY(true), 2)));
                    EmbeddedJDEC.VAL_RX.setText(String.valueOf(NumberUtils.roundTo(xbox.getX(false), 2)));
                    EmbeddedJDEC.VAL_RY.setText(String.valueOf(NumberUtils.roundTo(xbox.getY(false), 2)));
                    EmbeddedJDEC.VAL_Z.setText(" N/A");
                }
            } else {
                EmbeddedJDEC.VAL_X.setText(" N/A");
                EmbeddedJDEC.VAL_Y.setText(" N/A");
                EmbeddedJDEC.VAL_Z.setText(" N/A");
                EmbeddedJDEC.VAL_RX.setText(" N/A");
                EmbeddedJDEC.VAL_RY.setText(" N/A");
            }
        }
    }
}
