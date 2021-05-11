package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.elements.HideableLabel;
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
import java.util.ArrayList;
import java.util.List;

public class JoystickFrame extends PopupBase {
    private ValueUpdater valueUpdater;

    public JoystickFrame() {
        super("Joysticks", new Dimension(500, 265));
    }

    @Override
    public void config() {
        super.config();
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        EmbeddedJDEC.DISABLE_BTN.setVerticalTextPosition(SwingConstants.TOP);
        EmbeddedJDEC.DISABLE_BTN.setHorizontalTextPosition(SwingConstants.CENTER);

        EmbeddedJDEC.LIST.setVisibleRowCount(-1);
        EmbeddedJDEC.LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EmbeddedJDEC.LIST.setLayoutOrientation(JList.VERTICAL);
        EmbeddedJDEC.LIST_SCR.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setPreferredSize(new Dimension(200, 100));
        refreshControllerDisplay();

        EmbeddedJDEC.UP_BTN.setEnabled(false);
        EmbeddedJDEC.DOWN_BTN.setEnabled(false);
        EmbeddedJDEC.DISABLE_BTN.setEnabled(false);
        EmbeddedJDEC.INDEX_SET.setEnabled(false);
        EmbeddedJDEC.INDEX_SET.setColumns(4);

        EmbeddedJDEC.LIST.getSelectionModel().addListSelectionListener((e) -> {
            HIDDevice device = EmbeddedJDEC.LIST.getSelectedValue();
            if (device != null) {
                EmbeddedJDEC.INDEX_SET.setEnabled(true);
                EmbeddedJDEC.INDEX_SET.setText(String.valueOf(device.getIndex()));

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
                    if (i != 0 && i % 12 == 0) {
                        row += 2;
                    }
                    gbcButton.clone().setX(i % 12).setY(row).build(new JLabel(String.valueOf(i + 1)));
                    gbcButton.clone().setX(i % 12).setY(row + 1).build(cb);
                }
            } else {
                EmbeddedJDEC.INDEX_SET.setEnabled(false);
                EmbeddedJDEC.INDEX_SET.setText("");
            }
        });
        EmbeddedJDEC.UP_BTN.addActionListener(e -> {
            HIDDevice current = EmbeddedJDEC.LIST.getSelectedValue();
            swapDeviceIndices(current, current.getIndex() + 1);
        });
        EmbeddedJDEC.DOWN_BTN.addActionListener(e -> {
            HIDDevice current = EmbeddedJDEC.LIST.getSelectedValue();
            swapDeviceIndices(current, current.getIndex() - 1);
        });
        EmbeddedJDEC.DISABLE_BTN.addActionListener(e -> EmbeddedJDEC.LIST.getSelectedValue()
                .setDisabled(EmbeddedJDEC.DISABLE_BTN.isSelected()));
        EmbeddedJDEC.RELOAD_BTN.addActionListener(e -> refreshControllerDisplay());
        EmbeddedJDEC.CLOSE_BTN.addActionListener(e -> this.dispose());

        content.setLayout(new GridBagLayout());
        GBCPanelBuilder base = new GBCPanelBuilder(content).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(new Insets(5, 5, 5, 5));
        GBCPanelBuilder end = base.clone().setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END);

        base.clone().setPos(0, 0, 1, 1).setFill(GridBagConstraints.NONE).build(new JLabel("Index"));
        base.clone().setPos(0, 1, 1, 1).setFill(GridBagConstraints.NONE).build(EmbeddedJDEC.INDEX_SET);
        base.clone().setPos(0, 2, 1, 1).build(EmbeddedJDEC.RELOAD_BTN);

        base.clone().setPos(1, 0, 2, 3).build(EmbeddedJDEC.LIST_SCR);

        end.clone().setPos(4, 0, 1, 1).build(new JLabel("X: "));
        end.clone().setPos(4, 1, 1, 1).build(new JLabel("Y: "));
        end.clone().setPos(4, 2, 1, 1).build(new JLabel("Z: "));
        end.clone().setPos(6, 0, 1, 1).build(new JLabel("RX: "));
        end.clone().setPos(6, 1, 1, 1).build(new JLabel("RY: "));

        base.clone().setPos(5, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_X);
        base.clone().setPos(5, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Y);
        base.clone().setPos(5, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_Z);
        base.clone().setPos(7, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RX);
        base.clone().setPos(7, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.VAL_RY);

        base.clone().setPos(3, 0, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.UP_BTN);
        base.clone().setPos(3, 1, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.DOWN_BTN);
        base.clone().setPos(3, 2, 1, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.DISABLE_BTN);

        base.clone().setPos(0, 3, 7, 1).setAnchor(GridBagConstraints.LINE_START).build(EmbeddedJDEC.BUTTON_GRID);

        base.clone().setPos(0, 4, 8, 1).build(EmbeddedJDEC.CLOSE_BTN);

        EmbeddedJDEC.BUTTON_GRID.setLayout(new GridBagLayout());

        if (valueUpdater == null || !valueUpdater.isAlive()) {
            valueUpdater = new ValueUpdater();
        }
        valueUpdater.start();
    }

    private void refreshControllerDisplay() {
        EmbeddedJDEC.LIST_MODEL.clear();
        USBInterface.findControllers();
        for (HIDDevice hid : USBInterface.getControlDevices().values()) {
            EmbeddedJDEC.LIST_MODEL.add(EmbeddedJDEC.LIST_MODEL.size(), hid);
        }
    }

    @Override
    public void dispose() {
        if (valueUpdater != null) {
            valueUpdater.interrupt();
        }
        super.dispose();
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

        JTextField INDEX_SET = new JTextField("");
        JButton RELOAD_BTN = new JButton("↻");
        StickyButton CLOSE_BTN = new StickyButton("Close", 5);

        JButton UP_BTN = new JButton("↑");
        JButton DOWN_BTN = new JButton("↓");

        JPanel BUTTON_GRID = new JPanel();
        List<JCheckBox> BUTTONS = new ArrayList<>();

        JCheckBox DISABLE_BTN = new JCheckBox("Disable");
    }

    private static void swapDeviceIndices(HIDDevice current, int nIndex) {
        try {
            int cIndex = current.getIndex();
            if (cIndex != nIndex) {
                if (nIndex >= HIDDevice.MAX_JS_NUM) {
                    MessageBox.show("Index \"" + nIndex +
                        "\" greater than maximum joystick index of \"" +
                        HIDDevice.MAX_JS_NUM, MessageBox.Type.ERROR);
                    EmbeddedJDEC.INDEX_SET.setText(String.valueOf(cIndex));
                    return;
                }
                for (HIDDevice dev : USBInterface.getControlDevices().values()) {
                    if (dev.getIndex() == nIndex) {
                        MessageBox.show("Duplicate index \"" + nIndex + "\" for controller \"" + dev.toString()
                                + "\"\nSetting controller \"" + dev.toString() + "\" on index \"" + dev.getIndex()
                                + "\"\n to new index \"" + cIndex + "\" and making requested index change",
                            MessageBox.Type.WARNING);
                        dev.setIndex(cIndex);
                    }
                }
            }
            current.setIndex(nIndex);
            USBInterface.reindexControllers();
        } catch (NumberFormatException ignored) {
        }
    }

    public static class ValueUpdater extends Clock {
        public ValueUpdater() {
            super(100);
        }

        @Override
        public void onCycle() {
            HIDDevice current = EmbeddedJDEC.LIST.getSelectedValue();
            if (current != null) {
                EmbeddedJDEC.UP_BTN.setEnabled(current.getIndex() != 0);
                EmbeddedJDEC.DOWN_BTN.setEnabled(current.getIndex() != EmbeddedJDEC.LIST_MODEL.size() - 1);
                USBInterface.updateValues();
                swapDeviceIndices(current, Integer.parseInt(EmbeddedJDEC.INDEX_SET.getText()));
                if (current instanceof Joystick) {
                    Joystick js = (Joystick) current;
                    EmbeddedJDEC.VAL_X.setText(NumberUtils.roundTo(js.getX(), 2));
                    EmbeddedJDEC.VAL_Y.setText(NumberUtils.roundTo(js.getY(), 2));
                    EmbeddedJDEC.VAL_Z.setText(NumberUtils.roundTo(js.getZ(), 2));
                    EmbeddedJDEC.VAL_RX.setText(" N/A");
                    EmbeddedJDEC.VAL_RY.setText(" N/A");
                } else if (current instanceof XboxController) {
                    XboxController xbox = (XboxController) current;
                    EmbeddedJDEC.VAL_X.setText(NumberUtils.roundTo(xbox.getX(true), 2));
                    EmbeddedJDEC.VAL_Y.setText(NumberUtils.roundTo(xbox.getY(true), 2));
                    EmbeddedJDEC.VAL_RX.setText(NumberUtils.roundTo(xbox.getX(false), 2));
                    EmbeddedJDEC.VAL_RY.setText(NumberUtils.roundTo(xbox.getY(false), 2));
                    EmbeddedJDEC.VAL_Z.setText(" N/A");
                }
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
            }
        }
    }
}
