package com.boomaa.opends.usb;

public class LinuxController extends Controller<LinuxComponent> {
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

    private native long open(int idx);

    private native int getNumAxes(long address);

    private native int getNumButtons(long address);

    public int getIndex() {
        return idx;
    }

    @Override
    public Type getType() {
        //TODO add other input types
        return getName().contains("X-Box") ? Type.HID_GAMEPAD : Type.HID_JOYSTICK;
    }

    private native LinuxJSEvent poll(long address);

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

    private native String getName(long address);

    @Override
    public String getName() {
        return name;
    }

    private native void close(long address);
}
