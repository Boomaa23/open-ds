package com.boomaa.opends.usb;

import com.boomaa.opends.util.UnzipUtils;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class USBInterface {
    private static List<HIDDevice> controlDevices = new ArrayList<>();
    private static Controller[] rawControllers;
    private static boolean hasLibraryInit = false;

    static {
        // Workaround to JInput natives not being bundled with jinput
        //  extracts all natives to system temp folder from jar
        //  throws an illegal reflection error (not exception)
        initLibraries(false);
        refreshControllers();
    }

    public static void initLibraries(boolean force) {
        if (!hasLibraryInit || force) {
            String tmpPath = System.getProperty("java.io.tmpdir");
            UnzipUtils.unzip(USBInterface.getJarPath(), tmpPath);
            System.setProperty("java.library.path", tmpPath);
            try {
                MethodHandles.Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
                VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
                sys_paths.set(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            hasLibraryInit = true;
        }
    }

    private static ControllerEnvironment createDefaultEnvironment() {
        // More reflection workarounds to get JInput to rescan usb devices each time this method is called
        final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (final Thread thread : threadSet) {
            final String name = thread.getClass().getName();
            if (name.equals("net.java.games.input.RawInputEventQueue$QueueThread")) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (final InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
        try {
            Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                    Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void refreshControllers() {
        rawControllers = createDefaultEnvironment().getControllers();
        controlDevices.clear();
        for (int i = 0; i < rawControllers.length; i++) {
            if (rawControllers[i].getType() == Controller.Type.STICK) {
                controlDevices.add(new Joystick(rawControllers[i], controlDevices.size()));
            } else if (rawControllers[i].getType() == Controller.Type.GAMEPAD) {
                controlDevices.add(new XboxController(rawControllers[i], controlDevices.size()));
            }
        }
    }

    public static void updateValues() {
        for (int i = 0; i < controlDevices.size(); i++) {
            HIDDevice ctrl = controlDevices.get(i);
            Controller controller = ctrl.getController();
            if (!controller.poll()) {
                controlDevices.remove(ctrl);
                continue;
            }
            if (ctrl instanceof Joystick) {
                Component ax = controller.getComponent(Component.Identifier.Axis.X);
                Component ay = controller.getComponent(Component.Identifier.Axis.Y);
                Component arz = controller.getComponent(Component.Identifier.Axis.RZ);
                Joystick js = (Joystick) ctrl;
                js.setX(ax != null ? ax.getPollData() : 0.0);
                js.setY(ay != null ? ay.getPollData() : 0.0);
                js.setZ(arz != null ? arz.getPollData() : 0.0);
            } else if (ctrl instanceof XboxController) {
                Component axl = controller.getComponent(Component.Identifier.Axis.X);
                Component axr = controller.getComponent(Component.Identifier.Axis.RX);
                Component ayl = controller.getComponent(Component.Identifier.Axis.Y);
                Component ayr = controller.getComponent(Component.Identifier.Axis.RY);
                XboxController xbox = (XboxController) ctrl;
                xbox.setX(axl != null ? axl.getPollData() : 0.0, true);
                xbox.setX(axr != null ? axr.getPollData() : 0.0, false);
                xbox.setY(ayl != null ? ayl.getPollData() : 0.0, true);
                xbox.setY(ayr != null ? ayr.getPollData() : 0.0, false);
            }
            for (Component comp : controller.getComponents()) {
                if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                    try {
                        ctrl.setButton(Integer.parseInt(comp.getIdentifier().getName()), comp.getPollData() == 1.0);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    public static String getJarPath() {
        try {
            return new File(USBInterface.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Controller[] getRawControllers() {
        return rawControllers;
    }

    public static List<HIDDevice> getControlDevices() {
        return controlDevices;
    }
}