package com.boomaa.opends.usb;

import java.util.Map;

public class IOKitDevice extends Controller<IOKitElement> {
    private final long address;
    private final long ifaceAddr;
    private final Map<String, ?> properties;

    public IOKitDevice(long address, long ifaceAddr) {
        this.address = address;
        this.ifaceAddr = ifaceAddr;
        this.properties = getDeviceProperties(address);
        open(ifaceAddr);
        poll();
    }

    @SuppressWarnings("unchecked")
    private void addElements(Object[] elementProps) {
        if (elementProps == null) {
            return;
        }
        for (Object elementProp : elementProps) {
            Map<String, ?> singleEP = (Map<String, ?>) elementProp;
            long cookie = (Long) singleEP.get(IOKitFlags.kIOHIDElementCookieKey);
            int type = (Integer) singleEP.get(IOKitFlags.kIOHIDElementTypeKey);
            int min = getPropIntWithDef(singleEP, IOKitFlags.kIOHIDElementMinKey, IOKitFlags.AXIS_DEFAULT_MIN_VALUE);
            int max = getPropIntWithDef(singleEP, IOKitFlags.kIOHIDElementMaxKey, IOKitFlags.AXIS_DEFAULT_MAX_VALUE);
            int usage = (Integer) singleEP.get(IOKitFlags.kIOHIDElementUsageKey);
            int usagePage = (Integer) singleEP.get(IOKitFlags.kIOHIDElementUsagePageKey);
            objects.add(new IOKitElement(cookie, type, min, max, usage, usagePage));
            addElements(elementProps);
        }
    }
    
    private static Integer getPropIntWithDef(Map<String, ?> props, String key, int def) {
        Integer value = (Integer) props.get(key);
        return value != null ? value : def;
    }

    public native int open(long ifaceAddr);

    private native Map<String, ?> getDeviceProperties(long address);

    @Override
    public Type getType() {
        //TODO support more types
        return Type.HID_JOYSTICK;
    }

    @Override
    public void poll() {
        if (objects.isEmpty()) {
            addElements((Object[]) properties.get(IOKitFlags.kIOHIDElementKey));
        }
        for (Component comp : getComponents()) {
            IOKitElement element = (IOKitElement) comp;
            element.setValue(getElementValue(ifaceAddr, element.getCookie()).getValue());
        }
    }

    private native IOKitEvent getElementValue(long ifaceAddr, long elementCookie);

    @Override
    public String getName() {
        return (String) properties.get(IOKitFlags.kIOHIDProductKey);
    }

    public native int release(long address, long ifaceAddr);

    public native int close(long ifaceAddr);
}
