package com.boomaa.opends.usb.input;

public interface Controller {
    Type getType();
    Component[] getComponents();
    Component getComponent(Component.Identifier id);
    void poll();
    String getName();
    int getNumButtons();

    enum Type implements TypeEnum {
        UNKNOWN, GAMEPAD, STICK
    }

    interface TypeEnum {
        int ordinal();
        String name();

        default String getName() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
