package com.boomaa.opends.usb;

public class PlaceholderWindow {
    private static final PlaceholderWindow INSTANCE = new PlaceholderWindow();
    private final long hwndAddress;

    private PlaceholderWindow() {
        this.hwndAddress = createWindow();
    }

    private static native long createWindow();

    public final void destroy() {
        destroy(hwndAddress);
    }

    private static native void destroy(long hwndAddress);

    public final long getHwnd() {
        return hwndAddress;
    }

    public static PlaceholderWindow getInstance() {
        return INSTANCE;
    }
}
