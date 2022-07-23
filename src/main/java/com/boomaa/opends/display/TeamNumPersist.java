package com.boomaa.opends.display;

import com.boomaa.opends.util.OperatingSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TeamNumPersist {
    private static final String CONF_FN = "ods-teamnum.conf";

    private TeamNumPersist() {
    }

    public static String load() {
        File cached = getCachedFile();
        if (cached.exists()) {
            try {
                Scanner scan = new Scanner(cached);
                return scan.nextLine();
            } catch (FileNotFoundException | NoSuchElementException ignored) {
            }
        }
        return "";
    }

    public static void save(String teamNum) {
        if (teamNum.equals("Team Number")) {
            teamNum = "";
        }
        File cached = getCachedFile();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cached, false));
            writer.write(teamNum + "\n");
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
    }

    private static File getCachedFile() {
        return new File(OperatingSystem.getTempFolder() + CONF_FN);
    }
}
