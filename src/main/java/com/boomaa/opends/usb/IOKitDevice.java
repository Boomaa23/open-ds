package com.boomaa.opends.usb;

import java.util.Map;

public class IOKitDevice extends Controller<IOKitElement> {
    private final long address;
    private final long ifaceAddr;
    private final Map<String, ?> properties;
    private final long usagePage;
    private final long usage;
    private final Type hidType;

    public IOKitDevice(long address, long ifaceAddr) {
        this.address = address;
        this.ifaceAddr = ifaceAddr;
        this.properties = getDeviceProperties(address);
        this.usagePage = (Long) properties.get("PrimaryUsagePage");
        this.usage = (Long) properties.get("PrimaryUsage");
        this.hidType = usage == IOKitFlags.USAGE_GAMEPAD ? Type.HID_GAMEPAD :
                (usage == IOKitFlags.USAGE_JOYSTICK ? Type.HID_JOYSTICK : Type.UNKNOWN);
        open(ifaceAddr);
    }

    public long getAddress() {
        return address;
    }

    @SuppressWarnings("unchecked")
    private void addElements(Map<String, ?> mapProps) {
        Object[] elementProps = (Object[]) mapProps.get(IOKitFlags.kIOHIDElementKey);
        if (elementProps == null) {
            return;
        }
        for (Object elementProp : elementProps) {
            Map<String, ?> singleEP = (Map<String, ?>) elementProp;
            long cookie = (Long) singleEP.get(IOKitFlags.kIOHIDElementCookieKey);
            Long type = (Long) singleEP.get(IOKitFlags.kIOHIDElementTypeKey);
            int min = getPropIntWithDef(singleEP, IOKitFlags.kIOHIDElementMinKey, IOKitFlags.AXIS_DEFAULT_MIN_VALUE);
            int max = getPropIntWithDef(singleEP, IOKitFlags.kIOHIDElementMaxKey, IOKitFlags.AXIS_DEFAULT_MAX_VALUE);
            Long usage = (Long) singleEP.get(IOKitFlags.kIOHIDElementUsageKey);
            Long usagePage = (Long) singleEP.get(IOKitFlags.kIOHIDElementUsagePageKey);
            IOKitElement e = new IOKitElement(cookie, type.intValue(), min, max, usage.intValue(), usagePage.intValue());
            if (e.getIdentitifer() != Component.NullIdentifier.NONE) {
                if (e.isButton()) {
                    super.incrementNumButtons();
                } else if (e.isAxis()) {
                    super.incrementNumAxes();
                }
                objects.add(e);
            }
            addElements((Map<String, ?>) elementProp);
        }
    }
    
    private static Integer getPropIntWithDef(Map<String, ?> props, String key, int def) {
        Long value = (Long) props.get(key);
        return value != null ? value.intValue() : def;
    }

    public long getUsagePage() {
        return usagePage;
    }

    public long getUsage() {
        return usage;
    }

    public native int open(long ifaceAddr);

    private native Map<String, ?> getDeviceProperties(long address);

    @Override
    public Type getType() {
        return hidType;
    }

    @Override
    public void poll() {
        if (objects.size() == 0) {
            addElements(properties);
        }
        for (IOKitElement element : getComponents()) {
            IOKitEvent e = getElementValue(ifaceAddr, element.getCookie());
            if (e != null) {
                element.setValue(e.getValue());
            } else {
                super.remove();
            }
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
