package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.usb.Joystick;
import com.boomaa.opends.usb.USBInterface;
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
        super("Joysticks", new Dimension(310, 210), true, true);
    }

    @Override
    public void config() {
        content.setLayout(new GridBagLayout());
        GBCPanelBuilder base = new GBCPanelBuilder(content).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(new Insets(5, 5, 5, 5));

        EmbeddedJDEC.LIST.setVisibleRowCount(-1);
        EmbeddedJDEC.LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EmbeddedJDEC.LIST.setLayoutOrientation(JList.VERTICAL);
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

        base.clone().setPos(0, 0, 1, 1).build(EmbeddedJDEC.UP_BTN);
        base.clone().setPos(0, 1, 1, 1).build(EmbeddedJDEC.DOWN_BTN);
        base.clone().setPos(0, 2, 1, 1).build(EmbeddedJDEC.RELOAD_BTN);

        base.clone().setPos(1, 0, 2, 3).build(EmbeddedJDEC.LIST_SCR);

        base.clone().setPos(3, 0, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("X: "));
        base.clone().setPos(3, 1, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Y: "));
        base.clone().setPos(3, 2, 1, 1).setAnchor(GridBagConstraints.LINE_END).setFill(GridBagConstraints.NONE).build(new JLabel("Z: "));
        base.clone().setPos(4, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_X);
        base.clone().setPos(4, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Y);
        base.clone().setPos(4, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Z);

        if (valUpdater == null || !valUpdater.isAlive()) {
            valUpdater = new ValUpdater();
        }
        valUpdater.start();
        super.config();
    }

    private void swap(int a, int b) {
        Joystick aStr = EmbeddedJDEC.LIST_MODEL.getElementAt(a);
        Joystick bStr = EmbeddedJDEC.LIST_MODEL.getElementAt(b);
        EmbeddedJDEC.LIST_MODEL.set(a, bStr);
        EmbeddedJDEC.LIST_MODEL.set(b, aStr);
    }

    private void refreshControllerDisplay() {
        EmbeddedJDEC.LIST_MODEL.clear();
        for (Joystick js : USBInterface.getJoysticks()) {
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
        DefaultListModel<Joystick> LIST_MODEL = new DefaultListModel<>();
        JList<Joystick> LIST = new JList<>(LIST_MODEL);
        JScrollPane LIST_SCR = new JScrollPane(LIST);

        JLabel VAL_X = new JLabel();
        JLabel VAL_Y = new JLabel();
        JLabel VAL_Z = new JLabel();

        JButton UP_BTN = new JButton("▲");
        JButton DOWN_BTN = new JButton("▼");
        JButton RELOAD_BTN = new JButton("↻");
    }

    public static class ValUpdater extends Clock {
        public ValUpdater() {
            super(20);
        }

        @Override
        public void onCycle() {
            Joystick current = EmbeddedJDEC.LIST.getSelectedValue();
            if (current != null) {
                EmbeddedJDEC.VAL_X.setText(String.valueOf(NumberUtils.roundTo(current.getX(), 2)));
                EmbeddedJDEC.VAL_Y.setText(String.valueOf(NumberUtils.roundTo(current.getY(), 2)));
                EmbeddedJDEC.VAL_Z.setText(String.valueOf(NumberUtils.roundTo(current.getZ(), 2)));
            } else {
                EmbeddedJDEC.VAL_X.setText(" N/A");
                EmbeddedJDEC.VAL_Y.setText(" N/A");
                EmbeddedJDEC.VAL_Z.setText(" N/A");
            }
        }
    }
}
