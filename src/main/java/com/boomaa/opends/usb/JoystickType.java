package com.boomaa.opends.usb;

public enum JoystickType {
    UNKNOWN(-1),
    XINPUT_UNKNOWN(0),
    XINPUT_GAMEPAD(1),
    XINPUT_WHEEL(2),
    XINPUT_ARCADE(3),
    XINPUT_FLIGHT_STICK(4),
    XINPUT_DANCE_PAD(5),
    XINPUT_GUITAR(6),
    XINPUT_GUITAR_2(7),
    XINPUT_DRUM_KIT(8),
    XINPUT_GUITAR_3(11),
    XINPUT_ARCADE_PAD(19),
    HID_JOYSTICK(20),
    HID_GAMEPAD(21),
    HID_DRIVING(22),
    HID_FLIGHT(23),
    HID_1ST_PERSON(24);

    private final int num;

    JoystickType(int num) {
        this.num = num;
    }

    public int numAsInt() {
        return num;
    }

    public byte numAsByte() {
        return (byte) num;
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

    public enum JSAxis {
        X, Y, Z, TWIST, THROTTLE;

        public int getInt() {
            return this.ordinal();
        }
    }

    public enum XboxAxis {
        LEFT_X, LEFT_Y, LEFT_TRIGGER, RIGHT_TRIGGER, RIGHT_X, RIGHT_Y;

        public int getInt() {
            return this.ordinal();
        }
    }
}
