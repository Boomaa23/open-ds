package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.elements.AxisComboBox;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.usb.Component;
import com.boomaa.opends.usb.ComponentTracker;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.NumberUtils;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReassignAxesFrame extends FrameBase {
    private static ValueUpdater valueUpdater;
    private static HIDDevice device;

    public ReassignAxesFrame() {
        super("Reassign Axes", new Dimension(240, 300));
    }

    @Override
    public void config() {
        super.config();
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        device = JoystickTab.EmbeddedJDEC.LIST.getSelectedValue();

        EmbeddedJDEC.CLOSE_BTN.addActionListener((l) -> {
            HIDDevice selectedDevice = JoystickTab.EmbeddedJDEC.LIST.getSelectedValue();
            if (selectedDevice != device) {
                MessageBox.show("Selected joystick device has changed or been removed."
                        + " Not making changes.", MessageBox.Type.ERROR);
                forceDispose();
                return;
            }
            ComponentTracker axesTracker = selectedDevice.getAxesTracker();
            for (int i = 0; i < EmbeddedJDEC.USED_AXES.length; i++) {
                AxisComboBox box = EmbeddedJDEC.USED_AXES[i];
                Component.Identifier hwId = box.getHardwareId();
                Component.Axis userId = box.getUserId();
                if (hwId == Component.NullIdentifier.NONE) {
                    axesTracker.unmap(userId);
                } else {
                    axesTracker.map(userId, hwId, true);
                }
            }
            axesTracker.saveToFile(device.getAxesTrackerFilePath());
            forceDispose();
        });

        EmbeddedJDEC.LIST.setVisibleRowCount(-1);
        EmbeddedJDEC.LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EmbeddedJDEC.LIST.setLayoutOrientation(JList.VERTICAL);
        EmbeddedJDEC.LIST_SCR.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        EmbeddedJDEC.LIST_SCR.setPreferredSize(new Dimension(80, 100));

        GBCPanelBuilder base = new GBCPanelBuilder(content)
                .setFill(GridBagConstraints.BOTH)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(new Insets(5, 5, 5, 5));
        GBCPanelBuilder end = base.clone()
                .setFill(GridBagConstraints.NONE)
                .setAnchor(GridBagConstraints.LINE_END);

        base.clone().setPos(0, 0, 2, 1).build(new JLabel("Used Axes"));
        ComponentTracker axesTracker = device.getAxesTracker();
        List<Component.Identifier> allAxes = new ArrayList<>(axesTracker.getHardwareMap().keySet());
        for (int i = 0; i < EmbeddedJDEC.USED_AXES.length; i++) {
            AxisComboBox box = EmbeddedJDEC.USED_AXES[i];
            box.removeAllItems();
            box.addItem(Component.NullIdentifier.NONE);
            for (Component.Identifier axis : allAxes) {
                box.addItem(axis);
            }
            if (axesTracker.getUserMap().containsKey(box.getUserId())) {
                box.setSelectedItem(axesTracker.getUserMap().get(box.getUserId()));
            }
            end.clone().setPos(0, 1 + i, 1, 1).build(new JLabel(box.getName()));
            base.clone().setPos(1, 1 + i, 1, 1).build(box);
        }

        base.clone().setPos(2, 0, 2, 1).build(new JLabel("Available Axes"));
        EmbeddedJDEC.LIST_MODEL.clear();
        for (Component.Identifier axis : allAxes) {
            EmbeddedJDEC.LIST_MODEL.addElement(axis);
        }
        base.clone().setPos(2, 1, 2, 5).build(EmbeddedJDEC.LIST_SCR);
        base.clone().setPos(2, 6, 1, 1).build(new JLabel("Value:"));
        base.clone().setPos(3, 6, 1, 1).build(EmbeddedJDEC.AXIS_VALUE);

        base.clone().setPos(0, 7, 4, 1).build(EmbeddedJDEC.CLOSE_BTN);

        if (valueUpdater == null) {
            valueUpdater = new ValueUpdater();
            valueUpdater.start();
        }
    }

    @Override
    public void dispose() {
        forceDispose();
        super.dispose();
    }

    public interface EmbeddedJDEC {
        AxisComboBox[] USED_AXES = new AxisComboBox[] {
                new AxisComboBox(Component.Axis.X),
                new AxisComboBox(Component.Axis.Y),
                new AxisComboBox(Component.Axis.Z),
                new AxisComboBox(Component.Axis.RX),
                new AxisComboBox(Component.Axis.RY),
                new AxisComboBox(Component.Axis.RZ)
        };

        DefaultListModel<Component.Identifier> LIST_MODEL = new DefaultListModel<>();
        JList<Component.Identifier> LIST = new JList<>(LIST_MODEL);
        JScrollPane LIST_SCR = new JScrollPane(LIST);

        JLabel AXIS_VALUE = new JLabel(" N/A");

        JButton CLOSE_BTN = new JButton("Close");
    }

    private static class ValueUpdater extends Clock {
        private ValueUpdater() {
            super(100);
        }

        @Override
        public void onCycle() {
            if (!FrameBase.isAlive(ReassignAxesFrame.class)) {
                return;
            }
            Component.Identifier id = EmbeddedJDEC.LIST.getSelectedValue();
            HIDDevice selectedDevice = JoystickTab.EmbeddedJDEC.LIST.getSelectedValue();
            if (id != null && selectedDevice != null && selectedDevice == device) {
                Map<Component.Identifier, Integer> hwMap = device.getAxesTracker().getHardwareMap();
                double value = device.getComponent(hwMap.get(id)).getValue();
                EmbeddedJDEC.AXIS_VALUE.setText(String.valueOf(NumberUtils.roundTo(value, 2)));
            } else {
                EmbeddedJDEC.AXIS_VALUE.setText(" N/A");
            }
        }
    }
}
