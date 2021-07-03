package com.boomaa.opends.usb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class LinuxJoystickAPI extends NativeUSBManager<LinuxController> {
    public LinuxJoystickAPI() {
        enumDevices();
    }

    @Override
    public void enumDevices() {
        List<Integer> jsNums = new LinkedList<>();
        try {
            Object[] input = Files.list(Paths.get("/dev/input/")).toArray();
            for (Object obj : input) {
                String path = obj.toString();
                if (path.contains("js")) {
                    int idx = Integer.parseInt(path.substring(path.lastIndexOf("js") + 2));
                    LinuxController ctrl = new LinuxController(idx);
                    boolean inDevices = false;
                    for (LinuxController c : devices) {
                        if (c.getIndex() == idx && c.getName().equals(ctrl.getName())) {
                            inDevices = true;
                            break;
                        }
                    }
                    if (!inDevices) {
                        devices.add(ctrl);
                    }
                    jsNums.add(idx);
                }
            }
            for (LinuxController c : devices) {
                if (!jsNums.contains(c.getIndex())) {
                    c.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
