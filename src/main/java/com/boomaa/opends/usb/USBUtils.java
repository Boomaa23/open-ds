package com.boomaa.opends.usb;

import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.UnzipUtils;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class USBUtils {
    public static List<Joystick> JOYSTICKS = new ArrayList<>();
    private static Controller[] controllers;
    private static Event event;
    private static USBUpdater updater;

    static {
        // Workaround to JInput natives not being bundled with jinput
        //  extracts all natives to system temp folder from jar
        String tmpPath = System.getProperty("java.io.tmpdir");
        UnzipUtils.unzip(USBUtils.getJarPath(), tmpPath);
        System.setProperty("java.library.path", tmpPath);
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void refreshDevices() {
        event = new Event();
        controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        JOYSTICKS.clear();
        for (int i = 0; i < controllers.length; i++) {
            if (controllers[i].getType() == Controller.Type.STICK) {
                int btnCtr = 0;
                for (Component comp : controllers[i].getComponents()) {
                    if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                        btnCtr++;
                    }
                }
                JOYSTICKS.add(new Joystick(controllers[i], btnCtr));
            }
        }
        if (updater == null || !updater.isAlive()) {
            updater = new USBUpdater();
            updater.start();
        }
    }

    public static class USBUpdater extends Clock {
        public USBUpdater() {
            super(10);
        }

        @Override
        public void onCycle() {
            for (int i = 0; i < JOYSTICKS.size(); i++) {
                Joystick js = JOYSTICKS.get(i);
                Controller controller = js.getController();
                controller.poll();
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
}