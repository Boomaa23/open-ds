package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.Debug;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;

public class AutoOrderFrame extends FrameBase {
    private static final List<HIDDevice> devices = new LinkedList<>();
    private static int idxCtr = 0;
    private ValueUpdater valueUpdater;

    public AutoOrderFrame() {
        super("Joystick Auto Order", new Dimension(350, 260));
    }

    @Override
    public void preConfig() {
        super.preConfig();

        devices.clear();
        EmbeddedJDEC.SKIP_BTN.addActionListener(e -> {
            EmbeddedJDEC.JS_NAMES[idxCtr++].setText("<skipped>");
            if (idxCtr == EmbeddedJDEC.JS_NAMES.length) {
                EmbeddedJDEC.SKIP_BTN.setEnabled(false);
            }
        });

        EmbeddedJDEC.DONE_BTN.addActionListener(e -> {
            JoystickTab.EmbeddedJDEC.LIST_MODEL.clear();
            for (HIDDevice device : devices) {
                JoystickTab.EmbeddedJDEC.LIST_MODEL.add(JoystickTab.EmbeddedJDEC.LIST_MODEL.size(), device);
            }
            this.dispose();
        });

        content.setLayout(new GridBagLayout());
        GBCPanelBuilder base = new GBCPanelBuilder(content)
            .setFill(GridBagConstraints.BOTH)
            .setAnchor(GridBagConstraints.CENTER)
            .setInsets(new Insets(5, 5, 5, 5));

        base.clone().setPos(0, 0, 6, 1).build(new JLabel("Press a button on each joystick in ascending index order"));

        for (int i = 0; i < EmbeddedJDEC.JS_NAMES.length; i++) {
            base.clone().setPos(0, i + 1, 1, 1).build(new JLabel(i + ": "));
            base.clone().setPos(1, i + 1, 5, 1).build(EmbeddedJDEC.JS_NAMES[i]);
        }

        //TODO spacing/GBC positioning does not work for these buttons
        base.clone().setPos(0, 7, 3, 1).build(EmbeddedJDEC.SKIP_BTN);
        base.clone().setPos(3, 7, 3, 1).build(EmbeddedJDEC.DONE_BTN);

        if (valueUpdater == null) {
            valueUpdater = new ValueUpdater();
            Debug.println("Auto Order Frame value updater thread instantiated");
        }
        valueUpdater.start();
        Debug.println("Auto Order Frame value updater thread started");
    }

    @Override
    public void postConfig() {
        // setDefaultButton does not work
        // Focus request must occur after frame is visible
        EmbeddedJDEC.DONE_BTN.requestFocus();
    }

    @Override
    public void dispose() {
        for (JLabel label : EmbeddedJDEC.JS_NAMES) {
            label.setText("");
        }
        idxCtr = 0;
        EmbeddedJDEC.SKIP_BTN.setEnabled(true);
        if (valueUpdater != null) {
            Debug.println("Auto Order Frame value updater thread stopped");
            valueUpdater.end();
        }
        super.forceDispose();
    }

    public interface EmbeddedJDEC {
        JLabel[] JS_NAMES = new JLabel[] {
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel()
        };
        JButton SKIP_BTN = new JButton("Skip");
        JButton DONE_BTN = new JButton("Done");
    }

    public static class ValueUpdater extends Clock {
        public ValueUpdater() {
            super(100);
        }

        @Override
        public void onCycle() {
            ControlDevices.updateValues();
            for (HIDDevice device : ControlDevices.getAll().values()) {
                if (devices.contains(device)) {
                    continue;
                }
                boolean[] btns = device.getButtons();
                for (boolean b : btns) {
                    if (b) {
                        device.setIdx(idxCtr);
                        EmbeddedJDEC.JS_NAMES[idxCtr++].setText(device.getName());
                        devices.add(device);
                    }
                }
            }
        }
    }
}
