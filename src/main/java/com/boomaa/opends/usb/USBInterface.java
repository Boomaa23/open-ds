package com.boomaa.opends.usb;

import com.boomaa.opends.util.UnzipUtils;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class USBInterface {
    private static List<Joystick> joysticks = new ArrayList<>();
    private static Controller[] controllers;

    static {
        // Workaround to JInput natives not being bundled with jinput
        //  extracts all natives to system temp folder from jar
        //  throws an illegal reflection error (not exception)
        String tmpPath = System.getProperty("java.io.tmpdir");
        UnzipUtils.unzip(USBInterface.getJarPath(), tmpPath);
        System.setProperty("java.library.path", tmpPath);
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        // Allow below environment workaround to work without issues on windows 8/8.1
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            String os = System.getProperty("os.name", "").trim();
            if (os.startsWith("Windows 8")) {
                System.setProperty("jinput.useDefaultPlugin", "false");
                System.setProperty("net.java.games.input.plugins", "net.java.games.input.DirectAndRawInputEnvironmentPlugin");
            }
            return null;
        });
    }

    private static ControllerEnvironment createDefaultEnvironment() {
        // More reflection workarounds to get JInput to rescan usb devices each time this method is called
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
        controllers = createDefaultEnvironment().getControllers();
        joysticks.clear();
        for (int i = 0; i < controllers.length; i++) {
            if (controllers[i].getType() == Controller.Type.STICK) {
                int btnCtr = 0;
                for (Component comp : controllers[i].getComponents()) {
                    if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                        btnCtr++;
                    }
                }
                joysticks.add(new Joystick(controllers[i], btnCtr));
            }
        }
    }

    public static void updateValues() {
        if (joysticks.size() == 0) {
            refreshControllers();
        }
        for (int i = 0; i < joysticks.size(); i++) {
            Joystick js = joysticks.get(i);
            Controller controller = js.getController();
            if (!controller.poll()) {
                joysticks.remove(js);
                continue;
            }
            Component ax = controller.getComponent(Component.Identifier.Axis.X);
            Component ay = controller.getComponent(Component.Identifier.Axis.Y);
            Component arz = controller.getComponent(Component.Identifier.Axis.RZ);
            js.setX(ax != null ? ax.getPollData() : 0.0);
            js.setY(ay != null ? ay.getPollData() : 0.0);
            js.setZ(arz != null ? arz.getPollData() : 0.0);
            for (Component comp : controller.getComponents()) {
                if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                    js.setButton(Integer.parseInt(comp.getIdentifier().getName()), comp.getPollData() == 1.0);
                }
            }
        }
    }

    public static String getJarPath() {
        return "C:\\Users\\Nikhil\\Desktop\\open-ds.jar";
        //TODO uncomment this out when not testing
//        try {
//            return new File(DisplayEndpoint.class.getProtectionDomain()
//                    .getCodeSource().getLocation().toURI()).getPath();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public static Controller[] getControllers() {
        return controllers;
    }

    public static List<Joystick> getJoysticks() {
        refreshControllers();
        return joysticks;
    }
}