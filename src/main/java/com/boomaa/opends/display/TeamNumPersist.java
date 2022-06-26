package com.boomaa.opends.display;

import com.boomaa.opends.util.OperatingSystem;
import com.boomaa.opends.util.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TeamNumPersist {
    private static String confFile = "ods-teamnum.conf";
    private static boolean hasInit = false;

    public static void init() {
        if (Parameter.TEAM_PERSIST_FILE.isPresent()) {
            confFile = Parameter.TEAM_PERSIST_FILE.getStringValue();
        }
        confFile = OperatingSystem.getTempFolder() + confFile;
        hasInit = true;
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
        if (!hasInit || teamNum.equals("Team Number")) {
            return;
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
        return new File(confFile);
    }
}
