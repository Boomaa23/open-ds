package com.boomaa.opends.display.tabs;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.FrameBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.elements.HideableLabel;
import com.boomaa.opends.display.frames.AutoOrderFrame;
import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.display.frames.ReassignAxesFrame;
import com.boomaa.opends.usb.Component;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.usb.IndexTracker;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.NumberUtils;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class JoystickTab extends TabBase {
    private static final int BTN_PER_ROW = 16;
    private ValueUpdater valueUpdater;

    public JoystickTab() {
        super(new Dimension(520, 275));
    }

    @Override
    public void config() {
        EmbeddedJDEC.DISABLE_BTN.setVerticalTextPosition(SwingConstants.TOP);
        EmbeddedJDEC.DISABLE_BTN.setHorizontalTextPosition(SwingConstants.CENTER);

        EmbeddedJDEC.LIST.setVisibleRowCount(-1);
        EmbeddedJDEC.LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EmbeddedJDEC.LIST.setLayoutOrientation(JList.VERTICAL);
        EmbeddedJDEC.LIST_SCR.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setPreferredSize(new Dimension(200, 100));
        ctrlToDisplay();

        EmbeddedJDEC.UP_BTN.setEnabled(false);
        EmbeddedJDEC.DOWN_BTN.setEnabled(false);
        EmbeddedJDEC.DISABLE_BTN.setEnabled(false);
        EmbeddedJDEC.INDEX_SET.setEnabled(false);
        EmbeddedJDEC.REASSIGN_AXES_BTN.setEnabled(false);
        EmbeddedJDEC.INDEX_SET.setColumns(4);

        EmbeddedJDEC.LIST.getSelectionModel().addListSelectionListener((e) -> {
            HIDDevice device = EmbeddedJDEC.LIST.getSelectedValue();
            if (device != null) {
                EmbeddedJDEC.INDEX_SET.setEnabled(true);
                EmbeddedJDEC.INDEX_SET.setText(String.valueOf(device.getIdx()));

                EmbeddedJDEC.DISABLE_BTN.setEnabled(true);
                EmbeddedJDEC.DISABLE_BTN.setSelected(device.isDisabled());

                EmbeddedJDEC.BUTTONS.clear();
                EmbeddedJDEC.BUTTON_GRID.removeAll();
                GBCPanelBuilder gbcButton = new GBCPanelBuilder(EmbeddedJDEC.BUTTON_GRID);
                boolean[] buttons = device.getButtons();
                int row = 0;
                for (int i = 0; i < buttons.length; i++) {
                    JCheckBox cb = new JCheckBox();
                    cb.setEnabled(false);
                    cb.setSelected(buttons[i]);
                    EmbeddedJDEC.BUTTONS.add(i, cb);
                    if (i != 0 && i % BTN_PER_ROW == 0) {
                        row += 2;
                    }
                    gbcButton.clone().setX(i % BTN_PER_ROW).setY(row).build(new JLabel(String.valueOf(i + 1)));
                    gbcButton.clone().setX(i % BTN_PER_ROW).setY(row + 1).build(cb);
                }
            } else {
                EmbeddedJDEC.INDEX_SET.setEnabled(false);
                EmbeddedJDEC.INDEX_SET.setText("");
            }
        });
        EmbeddedJDEC.UP_BTN.addActionListener(e -> {
            int idx = EmbeddedJDEC.LIST.getSelectedIndex();
            swapDeviceIndices(idx, idx - 1);
        });
        EmbeddedJDEC.DOWN_BTN.addActionListener(e -> {
            int idx = EmbeddedJDEC.LIST.getSelectedIndex();
            swapDeviceIndices(idx, idx + 1);
        });
        EmbeddedJDEC.AUTO_ORDER_BTN.addActionListener(e -> {
            if (!FrameBase.isAlive(AutoOrderFrame.class)) {
                new AutoOrderFrame();
            } else {
                FrameBase.getAlive(AutoOrderFrame.class).reopen();
            }
        });
        EmbeddedJDEC.REASSIGN_AXES_BTN.addActionListener(e -> {
            if (!FrameBase.isAlive(ReassignAxesFrame.class)) {
                new ReassignAxesFrame();
            } else {
                FrameBase.getAlive(ReassignAxesFrame.class).reopen();
            }
        });
        EmbeddedJDEC.DISABLE_BTN.addActionListener(e -> EmbeddedJDEC.LIST.getSelectedValue()
                .setDisabled(EmbeddedJDEC.DISABLE_BTN.isSelected()));
        EmbeddedJDEC.RELOAD_BTN.addActionListener(e -> resetControllerDisplay());

        super.setLayout(new GridBagLayout());
        GBCPanelBuilder base = new GBCPanelBuilder(this)
                .setFill(GridBagConstraints.BOTH)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(new Insets(5, 5, 5, 5));
        GBCPanelBuilder end = base.clone()
                .setFill(GridBagConstraints.NONE)
                .setAnchor(GridBagConstraints.LINE_END);

        base.clone().setPos(0, 0, 1, 1).setFill(GridBagConstraints.NONE).build(new JLabel("Index"));
        base.clone().setPos(0, 1, 1, 1).setFill(GridBagConstraints.NONE).build(EmbeddedJDEC.INDEX_SET);

        base.clone().setPos(1, 0, 2, 3).build(EmbeddedJDEC.LIST_SCR);

        base.clone().setPos(3, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.UP_BTN);
        base.clone().setPos(3, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.DOWN_BTN);
        base.clone().setPos(3, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.DISABLE_BTN);

        base.clone().setPos(4, 0, 1, 1).build(EmbeddedJDEC.RELOAD_BTN);
        base.clone().setPos(4, 1, 1, 1).build(EmbeddedJDEC.AUTO_ORDER_BTN);
        base.clone().setPos(4, 2, 1, 1).build(EmbeddedJDEC.REASSIGN_AXES_BTN);

        end.clone().setPos(5, 0, 1, 1).build(new JLabel("X: "));
        end.clone().setPos(5, 1, 1, 1).build(new JLabel("Y: "));
        end.clone().setPos(5, 2, 1, 1).build(new JLabel("Z: "));
        end.clone().setPos(7, 0, 1, 1).build(new JLabel("RX: "));
        end.clone().setPos(7, 1, 1, 1).build(new JLabel("RY: "));
        end.clone().setPos(7, 2, 1, 1).build(new JLabel("RZ: "));

        base.clone().setPos(6, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_X);
        base.clone().setPos(6, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Y);
        base.clone().setPos(6, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Z);
        base.clone().setPos(8, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RX);
        base.clone().setPos(8, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RY);
        base.clone().setPos(8, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RZ);

        base.clone().setPos(2, 3, 6, 2).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.BUTTON_GRID);

        EmbeddedJDEC.BUTTON_GRID.setLayout(new GridBagLayout());

        if (valueUpdater == null) {
            valueUpdater = new ValueUpdater();
        }
        valueUpdater.start();
    }

    private static synchronized void swapDeviceIndices(int aIndex, int bIndex) {
        HIDDevice aDevice = EmbeddedJDEC.LIST_MODEL.getElementAt(aIndex);
        try {
            HIDDevice bDevice = EmbeddedJDEC.LIST_MODEL.getElementAt(bIndex);
            aDevice.setIdx(bIndex);
            bDevice.setIdx(aIndex);
            EmbeddedJDEC.LIST_MODEL.set(aIndex, bDevice);
            EmbeddedJDEC.LIST_MODEL.set(bIndex, aDevice);
            EmbeddedJDEC.LIST.setSelectedIndex(bIndex);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            int listSize = EmbeddedJDEC.LIST_MODEL.size();
            aDevice.setIdx(bIndex);
            if (bIndex >= listSize) {
                EmbeddedJDEC.LIST_MODEL.removeElement(aDevice);
                EmbeddedJDEC.LIST_MODEL.addElement(aDevice);
                EmbeddedJDEC.LIST.setSelectedIndex(listSize - 1);
            }
        }
    }

    private void resetControllerDisplay() {
        EmbeddedJDEC.LIST_MODEL.clear();
        ControlDevices.clearAll();
        ControlDevices.findAll();
    }

    private void ctrlToDisplay() {
        for (HIDDevice hid : ControlDevices.getAll().values()) {
            EmbeddedJDEC.LIST_MODEL.add(EmbeddedJDEC.LIST_MODEL.size(), hid);
        }
    }

    public interface EmbeddedJDEC {
        DefaultListModel<HIDDevice> LIST_MODEL = new DefaultListModel<>();
        JList<HIDDevice> LIST = new JList<>(LIST_MODEL);
        JScrollPane LIST_SCR = new JScrollPane(LIST);

        HideableLabel VAL_X = new HideableLabel(true);
        HideableLabel VAL_Y = new HideableLabel(true);
        HideableLabel VAL_Z = new HideableLabel(true);
        HideableLabel VAL_RX = new HideableLabel(true);
        HideableLabel VAL_RY = new HideableLabel(true);
        HideableLabel VAL_RZ = new HideableLabel(true);

        JTextField INDEX_SET = new JTextField("");
        JButton RELOAD_BTN = new JButton("↻");
        JButton AUTO_ORDER_BTN = new JButton("Auto");
        JButton REASSIGN_AXES_BTN = new JButton("Axes");

        JButton UP_BTN = new JButton("↑");
        JButton DOWN_BTN = new JButton("↓");

        JPanel BUTTON_GRID = new JPanel();
        List<JCheckBox> BUTTONS = new ArrayList<>();

        JCheckBox DISABLE_BTN = new JCheckBox("Disable");
    }

    public static class ValueUpdater extends Clock {
        public ValueUpdater() {
            super(100);
        }

        @Override
        public void onCycle() {
            if (!TabBase.isVisible(JoystickTab.class) && !FrameBase.isVisible(AutoOrderFrame.class)) {
                return;
            }
            HIDDevice current = EmbeddedJDEC.LIST.getSelectedValue();
            int cListIdx = EmbeddedJDEC.LIST.getSelectedIndex();
            if (current != null) {
                EmbeddedJDEC.UP_BTN.setEnabled(cListIdx != 0);
                EmbeddedJDEC.DOWN_BTN.setEnabled(cListIdx != EmbeddedJDEC.LIST_MODEL.size() - 1);
                EmbeddedJDEC.REASSIGN_AXES_BTN.setEnabled(true);
                ControlDevices.updateValues();
                try {
                    int nFRCIdx = Integer.parseInt(EmbeddedJDEC.INDEX_SET.getText());
                    if (nFRCIdx >= IndexTracker.MAX_JS_NUM) {
                        MessageBox.show("New index " + nFRCIdx + " greater than maximum index of 6."
                                + " Not performing change.", MessageBox.Type.ERROR);
                        EmbeddedJDEC.INDEX_SET.setText(String.valueOf(cListIdx));
                    } else if (current.getIdx() != nFRCIdx) {
                        for (HIDDevice dev : ControlDevices.getAll().values()) {
                            if (dev.getIdx() == nFRCIdx) {
                                MessageBox.show("Duplicate index \"" + nFRCIdx + "\" for controller \"" + dev
                                        + "\"\nSetting controller \"" + dev + "\" on index \"" + dev.getIdx()
                                        + "\"\n to new index \"" + cListIdx + "\" and making requested index change",
                                    MessageBox.Type.WARNING);
                            }
                        }
                        MainJDEC.IS_ENABLED.setSelected(false);
                        swapDeviceIndices(cListIdx, nFRCIdx);
                        ControlDevices.reindexAll();
                    }
                } catch (NumberFormatException ignored) {
                }
                setAxisValue(current, EmbeddedJDEC.VAL_X, Component.Axis.X);
                setAxisValue(current, EmbeddedJDEC.VAL_Y, Component.Axis.Y);
                setAxisValue(current, EmbeddedJDEC.VAL_Z, Component.Axis.Z);
                setAxisValue(current, EmbeddedJDEC.VAL_RX, Component.Axis.RX);
                setAxisValue(current, EmbeddedJDEC.VAL_RY, Component.Axis.RY);
                setAxisValue(current, EmbeddedJDEC.VAL_RZ, Component.Axis.RZ);
                boolean[] buttons = current.getButtons();
                for (int i = 0; i < buttons.length && i < EmbeddedJDEC.BUTTONS.size(); i++) {
                    EmbeddedJDEC.BUTTONS.get(i).setSelected(buttons[i]);
                }
            } else {
                EmbeddedJDEC.VAL_X.setText(" N/A");
                EmbeddedJDEC.VAL_Y.setText(" N/A");
                EmbeddedJDEC.VAL_Z.setText(" N/A");
                EmbeddedJDEC.VAL_RX.setText(" N/A");
                EmbeddedJDEC.VAL_RY.setText(" N/A");
                EmbeddedJDEC.VAL_RZ.setText(" N/A");

                EmbeddedJDEC.REASSIGN_AXES_BTN.setEnabled(false);
                EmbeddedJDEC.UP_BTN.setEnabled(false);
                EmbeddedJDEC.DOWN_BTN.setEnabled(false);
            }
            EmbeddedJDEC.BUTTON_GRID.revalidate();
        }

        private void setAxisValue(HIDDevice device, JLabel label, Component.Axis axis) {
            double value = device.getAxis(axis);
            label.setText(value != Integer.MAX_VALUE ? String.valueOf(NumberUtils.roundTo(value, 2)) : " N/A");
        }
    }
}
