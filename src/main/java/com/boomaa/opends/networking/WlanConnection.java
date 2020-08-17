package com.boomaa.opends.networking;

import com.boomaa.opends.util.OperatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WlanConnection {
    private final String ssid;
    private final int signal; //percent

    public WlanConnection(String ssid, int signal) {
        this.ssid = ssid;
        this.signal = signal;
    }

    public String getSSID() {
        return ssid;
    }

    public int getSignal() {
        return signal;
    }

    //TODO this probably doesn't work considering we can't see the networks and the wifi interfaces will likely be disabled on DSs
    // might even be the wrong thing, the field says "Field Radio Metrics" (e.g. FMS AP) not "Robot Radio Metrics"
    public static WlanConnection getRadio(int teamNum) {
        try {
            OperatingSystem current = OperatingSystem.getCurrent();
            if (current == OperatingSystem.WINDOWS) {
                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show interfaces");
                builder.redirectErrorStream(true);
                Process p = builder.start();
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                boolean foundNet = false;
                while ((line = r.readLine()) != null) {
                    if (!foundNet) {
                        if (line.contains("SSID") && line.contains(" : " + teamNum)) {
                            foundNet = true;
                        }
                    } else if (line.contains("Signal")) {
                        return new WlanConnection(String.valueOf(teamNum), Integer.parseInt(line.substring(30).replaceAll("%", "")));
                    }
                }
            } else if (current == OperatingSystem.UNIX) {
                Process p = Runtime.getRuntime().exec("iwlist scan");
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                int lastsignal = -1;
                while ((line = r.readLine()) != null) {
                    if (line.contains("Quality")) {
                        String qualStr = line.substring(line.indexOf("=") + 1);
                        int iosl = qualStr.indexOf("/");
                        int iospc = qualStr.indexOf(" ");
                        lastsignal = (int) (Integer.parseInt(qualStr.substring(0, iosl)) / ((double) Integer.parseInt(qualStr.substring(iosl + 1, iospc))) * 100);
                    }
                    if (line.contains("SSID:\"" + teamNum)) {
                        return new WlanConnection(String.valueOf(teamNum), lastsignal);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
