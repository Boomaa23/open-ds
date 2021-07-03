package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.util.Clock;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

public class AutoOrderFrame extends PopupBase {
    private static int idxCtr = 0;
    private static List<HIDDevice> devices = new LinkedList<>();
    private ValueUpdater valueUpdater;

    public AutoOrderFrame() {
        super("Joystick Auto Order", new Dimension(350, 260));
    }

    @Override
    public void config() {
        super.config();

        devices.clear();
        if (EmbeddedJDEC.SKIP_BTN.getActionListeners().length == 0) {
            EmbeddedJDEC.SKIP_BTN.addActionListener(e -> {
                EmbeddedJDEC.JS_NAMES[idxCtr++].setText("<skipped>");
                if (idxCtr == EmbeddedJDEC.JS_NAMES.length) {
                    EmbeddedJDEC.SKIP_BTN.setEnabled(false);
                }
            });
        }

        EmbeddedJDEC.DONE_BTN.addActionListener(e -> {
            JoystickFrame.EmbeddedJDEC.LIST_MODEL.clear();
            for (HIDDevice device : devices) {
                JoystickFrame.EmbeddedJDEC.LIST_MODEL.add(JoystickFrame.EmbeddedJDEC.LIST_MODEL.size(), device);
            }
            this.dispose();
        });

        content.setLayout(new GridBagLayout());
        GBCPanelBuilder base = new GBCPanelBuilder(content).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(new Insets(5, 5, 5, 5));

        base.clone().setPos(0, 0, 6, 1).build(new JLabel("Press a button on each joystick in ascending index order"));

        base.clone().setPos(0, 1, 1, 1).build(new JLabel("0: "));
        base.clone().setPos(0, 2, 1, 1).build(new JLabel("1: "));
        base.clone().setPos(0, 3, 1, 1).build(new JLabel("2: "));
        base.clone().setPos(0, 4, 1, 1).build(new JLabel("3: "));
        base.clone().setPos(0, 5, 1, 1).build(new JLabel("4: "));
        base.clone().setPos(0, 6, 1, 1).build(new JLabel("5: "));

        base.clone().setPos(1, 1, 5, 1).build(EmbeddedJDEC.JS_NAMES[0]);
        base.clone().setPos(1, 2, 5, 1).build(EmbeddedJDEC.JS_NAMES[1]);
        base.clone().setPos(1, 3, 5, 1).build(EmbeddedJDEC.JS_NAMES[2]);
        base.clone().setPos(1, 4, 5, 1).build(EmbeddedJDEC.JS_NAMES[3]);
        base.clone().setPos(1, 5, 5, 1).build(EmbeddedJDEC.JS_NAMES[4]);
        base.clone().setPos(1, 6, 5, 1).build(EmbeddedJDEC.JS_NAMES[5]);

        //TODO spacing/GBC positioning does not work for these buttons
        base.clone().setPos(0, 7, 3, 1).build(EmbeddedJDEC.SKIP_BTN);
        base.clone().setPos(3, 7, 3, 1).build(EmbeddedJDEC.DONE_BTN);

        if (valueUpdater == null) {
            valueUpdater = new ValueUpdater();
        }
        valueUpdater.start();
    }

    @Override
    public void dispose() {
        for (JLabel label : EmbeddedJDEC.JS_NAMES) {
            label.setText("");
        }
        idxCtr = 0;
        EmbeddedJDEC.SKIP_BTN.setEnabled(true);
        if (valueUpdater != null) {
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
