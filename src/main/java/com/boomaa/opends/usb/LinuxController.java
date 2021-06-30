package com.boomaa.opends.usb;

import java.util.LinkedList;
import java.util.List;

public class LinuxController extends Controller {
    private final List<LinuxComponent> objects = new LinkedList<>();
    private final long address;
    private final int idx;
    private final String name;

    public LinuxController(int idx) {
        this.address = open(idx);
        this.idx = idx;
        this.name = getName(address);
        this.numAxes = getNumAxes(address);
        this.numButtons = getNumButtons(address);
        poll();
    }

    public native long open(int idx);

    public native int getNumAxes(long address);

    public native int getNumButtons(long address);

    public int getIndex() {
        return idx;
    }

    @Override
    public Type getType() {
        //TODO add other input types
        return Type.HID_JOYSTICK;
    }

    @Override
    public Component[] getComponents() {
        return objects.toArray(new Component[0]);
    }

    @Override
    public Component getComponent(Component.Identifier id) {
        for (Component comp : objects) {
            if (comp.getIdentitifer() == id) {
                return comp;
            }
        }
        return null;
    }

    public native LinuxJSEvent poll(long address);

    @Override
    public void poll() {
        LinuxJSEvent event;
        while ((event = poll(address)) != null && event.isValid()) {
            if ((event.getType() & LinuxFlags.JS_EVENT_INIT) != 0) {
                objects.add(new LinuxComponent(event));
            } else {
                boolean eventBtn = (event.getType() & LinuxFlags.JS_EVENT_BUTTON) != 0;
                boolean eventAxis = (event.getType() & LinuxFlags.JS_EVENT_AXIS) != 0;
                for (LinuxComponent comp : objects) {
                    if (comp.getNumber() == event.getNumber()) {
                        if (comp.isButton() && eventBtn) {
                            comp.setValue(event.getValue());
                            break;
                        } else if (comp.isAxis() && eventAxis) {
                            comp.setValue(event.getValue() / (double) Short.MAX_VALUE);
                            break;
                        }
                    }
                }
            }
        }
        if (event != null && !event.isValid()) {
            super.remove();
        }
    }

    public long getAddress() {
        return address;
    }

    public native String getName(long address);

    @Override
    public String getName() {
        return name;
    }

    public native void close(long address);
}
