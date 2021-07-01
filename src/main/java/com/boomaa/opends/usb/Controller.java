package com.boomaa.opends.usb;

import java.util.LinkedList;
import java.util.List;

public abstract class Controller<T extends Component> {
    protected final List<T> objects = new LinkedList<>();
    protected int numButtons;
    protected int numAxes;
    protected boolean queueRemove;

    public abstract Type getType();
    public abstract void poll();
    public abstract String getName();

    public Component[] getComponents() {
        return objects.toArray(new Component[0]);
    }

    public Component getComponent(Component.Identifier id) {
        for (Component comp : objects) {
            if (comp.getIdentitifer() == id) {
                return comp;
            }
        }
        return null;
    }

    public int getNumButtons() {
        return numButtons;
    }

    public int incrementNumButtons() {
        return ++numButtons;
    }

    public int getNumAxes() {
        return numAxes;
    }

    public int incrementNumAxes() {
        return ++numAxes;
    }

    public void remove() {
        queueRemove = true;
    }

    public boolean needsRemove() {
        return queueRemove;
    }

    @Override
    public String toString() {
        return getName();
    }

    public enum Type {
        UNKNOWN(-1, -1),
        XINPUT_UNKNOWN(0, -1),
        XINPUT_GAMEPAD(1, -1),
        XINPUT_WHEEL(2, -1),
        XINPUT_ARCADE(3, -1),
        XINPUT_FLIGHT_STICK(4, -1),
        XINPUT_DANCE_PAD(5, -1),
        XINPUT_GUITAR(6, -1),
        XINPUT_GUITAR_2(7, -1),
        XINPUT_DRUM_KIT(8, -1),
        XINPUT_GUITAR_3(11, -1),
        XINPUT_ARCADE_PAD(19, -1),
        HID_JOYSTICK(20, 0x14),
        HID_GAMEPAD(21, 0x15),
        HID_DRIVING(22, -1),
        HID_FLIGHT(23, -1),
        HID_1ST_PERSON(24, -1);

        private final int frcFlag;
        private final int diFlag;

        Type(int frcFlag, int diFlag) {
            this.frcFlag = frcFlag;
            this.diFlag = diFlag;
        }

        public int getDirectInputFlag() {
            return diFlag;
        }

        public int getFRCFlag() {
            return frcFlag;
        }

        public String nameAsString() {
            StringBuilder sb = new StringBuilder();
            String name = name();
            boolean nextCaps = false;
            for (int i = 0; i < name.length(); i++) {
                String tChar = name.substring(i, i + 1);
                if (!nextCaps) {
                    tChar = tChar.toLowerCase();
                } else {
                    nextCaps = false;
                }
                if (sb.charAt(i) == '_') {
                    nextCaps = true;
                }
                sb.append(tChar);
            }
            return sb.toString();
        }
    }
}
