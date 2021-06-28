package com.boomaa.opends.networking;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.OperatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WlanConnection extends Clock {
    private static WlanConnection RADIO;
    private final String ssid;
    private int signal; //percent

    public WlanConnection(String ssid) {
        super(1000);
        this.ssid = ssid;
    }

    public String getSSID() {
        return ssid;
    }

    public int getSignal() {
        return signal;
    }

    //TODO this probably doesn't work considering we can't see the networks and the wifi interfaces will likely be disabled on DSs
    // might even be the wrong thing, the field says "Field Radio Metrics" (e.g. FMS AP) not "Robot Radio Metrics"
    public static WlanConnection getRadio() {
        int teamNum = MainJDEC.TEAM_NUMBER.checkedIntParse();
        if (RADIO != null) {
            if (Integer.parseInt(RADIO.getSSID()) != teamNum) {
                RADIO.end();
            }
            RADIO = new WlanConnection(String.valueOf(teamNum));
            RADIO.start();
        }
        return RADIO;
    }

    @Override
    public void onCycle() {
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
                        if (line.contains("SSID") && line.contains(" : " + ssid)) {
                            foundNet = true;
                        }
                    } else if (line.contains("Signal")) {
                        signal = Integer.parseInt(line.substring(30).replaceAll("%", ""));
                        return;
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
                    if (line.contains("SSID:\"" + ssid)) {
                        signal = lastsignal;
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
