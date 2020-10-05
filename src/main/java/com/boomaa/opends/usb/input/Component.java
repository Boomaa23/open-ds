package com.boomaa.opends.usb.input;

public interface Component {
    Identifier getIdentitifer();
    double getDeadband();
    double getValue();
    String getName();

    interface Identifier extends Controller.TypeEnum {
        @Override
        default String getName() {
            String name = name();
            if (name.charAt(0) == '_') {
                return name.substring(1);
            } else if (name.contains("_")) {
                char[] nameChars = name.toCharArray();
                StringBuilder out = new StringBuilder();
                boolean nextCaps = true;
                for (char nameChar : nameChars) {
                    if (nextCaps) {
                        out.append(Character.toUpperCase(nameChar));
                        nextCaps = false;
                    } else if (nameChar == '_') {
                        out.append(' ');
                        nextCaps = true;
                    } else {
                        out.append(nameChar);
                    }
                }
                return out.toString();
            }
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    enum Axis implements Identifier {
        X, Y, Z, RX, RY, RZ, UNKNOWN
    }
    
    enum Button implements Identifier {
        _00, _01, _02, _03, _04, _05, _06, _07, _08, _09,
        _10, _11, _12, _13, _14, _15, _16, _17, _18, _19,
        _20, _21, _22, _23, _24, _25, _26, _27, _28, _29,
        _30, _31
    }

    enum NamedButton implements Identifier {
        TRIGGER, THUMB, THUMB_2, TOP, TOP_2, PINKIE,
        BASE, BASE_2, BASE_3, BASE_4, BASE_5, BASE_6,
        A, B, C, X, Y, Z,
        LEFT_THUMB, RIGHT_THUMB, LEFT_THUMB_2, RIGHT_THUMB_2,
        SELECT, START, MODE, LEFT_THUMB_3, RIGHT_THUMB_3, UNKNOWN
    }
}
