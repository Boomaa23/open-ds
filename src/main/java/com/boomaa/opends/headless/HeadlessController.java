package com.boomaa.opends.headless;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.networktables.NTEntry;
import com.boomaa.opends.networktables.NTStorage;
import com.boomaa.opends.util.OperatingSystem;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class HeadlessController implements HeadlessJDEC {
    public static final String LOGO_ASCII = ""
        + "   ____                   ____  _____\n"
        + "  / __ \\____  ___  ____  / __ \\/ ___/\n"
        + " / / / / __ \\/ _ \\/ __ \\/ / / /\\__ \\ \n"
        + "/ /_/ / /_/ /  __/ / / / /_/ /___/ / \n"
        + "\\____/ .___/\\___/_/ /_/_____//____/  \n"
        + "    /_/                              \n";
    private static final Scanner inputScanner = new Scanner(System.in);

    static {
        STATUS_TABLE.setRow(0, 0, "Key", "Value");
        STATUS_TABLE.setCol(1, 0, "Voltage", "Robot", "Code", "EStop", "FMS", "Time");
        STATUS_TABLE.setCol(1, 1,
            MainJDEC.BAT_VOLTAGE::getText,
            MainJDEC.ROBOT_CONNECTION_STATUS::getText,
            MainJDEC.ROBOT_CODE_STATUS::getText,
            MainJDEC.ESTOP_STATUS::getText,
            MainJDEC.FMS_CONNECTION_STATUS::getText,
            MainJDEC.MATCH_TIME::getText
        );

        STATISTICS_TABLE.setRow(0, 0, "Key", "Value");
        for (int i = 0; i < StatsFields.values().length; i++) {
            STATISTICS_TABLE.getEntry(i + 1, 0).setValue(StatsFields.values()[i].getKey());
        }

        for (RobotMode mode : RobotMode.values()) {
            CHANGE_MODE_TABLE.appendOption(mode.toString(), () -> {
                MainJDEC.ROBOT_DRIVE_MODE.setSelectedItem(mode);
                return OperationReturn.CONTINUE;
            });
        }
        CHANGE_MODE_TABLE.appendOption("(go back)", () -> OperationReturn.CONTINUE);

        for (int i = 1; i <= MainJDEC.ALLIANCE_NUM.getItemCount(); i++) {
            int finalI = i;
            CHANGE_ANUM_TABLE.appendOption(String.valueOf(i), () -> {
                MainJDEC.ALLIANCE_NUM.setSelectedItem(finalI);
                return OperationReturn.CONTINUE;
            });
        }
        CHANGE_ANUM_TABLE.appendOption("(go back)", () -> OperationReturn.CONTINUE);

        for (int i = 0; i < MainJDEC.ALLIANCE_COLOR.getItemCount(); i++) {
            String item = MainJDEC.ALLIANCE_COLOR.getItems()[i];
            CHANGE_ACOLOR_TABLE.appendOption(item, () -> {
                MainJDEC.ALLIANCE_COLOR.setSelectedItem(item);
                return OperationReturn.CONTINUE;
            });
        }
        CHANGE_ACOLOR_TABLE.appendOption("(go back)", () -> OperationReturn.CONTINUE);

        for (MainHAction action : MainHAction.values()) {
            MAIN_ACTIONS_TABLE.appendOption(action.getOptionName(), action.getOperation(), action.getSupplier());
        }

        for (Map.Entry<Integer, NTEntry> mapEntry : NTStorage.ENTRIES.entrySet()) {
            int id = mapEntry.getKey();
            NTEntry ntEntry = mapEntry.getValue();
            //TODO add every NTEntry to the shuffleboard table
            // may have to reinit the table each load b/c of fixed size (or rethink the variable width idea)
        }
    }

    private HeadlessController() {
    }

    public static void start() {
        printMenu();
        while (true) {
            OperationReturn retval = runOperation(MAIN_ACTIONS_TABLE);
            if (retval != OperationReturn.CONTINUE) {
                String response = "";
                while (!response.equals("y")) {
                    response = prompt("Return to menu? [y]: ").toLowerCase();
                }
            }

            clear();
            printMenu();
        }
    }

    public static OperationReturn runOperation(OptionTable table) {
        OperationReturn retval = null;
        while (retval == null) {
            String response = prompt("Select an action: ");
            if (response.length() != 1) {
                System.err.println("Input was too long. Please try again.");
                continue;
            }
            retval = table.runOperation(response.charAt(0));
            if (retval == null) {
                System.err.println("Invalid input. Please try again.");
            }
        }
        return retval;
    }

    public static void printMenu() {
        STATUS_TABLE.updateAll();
        MAIN_ACTIONS_TABLE.updateAll();
        System.out.println(LOGO_ASCII
            + "by Boomaa23\n"
            + "------------------\n\n"
            + "Status: \n"
            + STATUS_TABLE
            + "\n"
            + "Actions:\n"
            + MAIN_ACTIONS_TABLE
        );
    }

    public static String prompt(String message) {
        System.out.print(message);
        return getInput();
    }

    public static String getInput() {
        return inputScanner.nextLine();
    }

    public static void clear() {
        //TODO this might not work, fix if it does not
        try {
            (OperatingSystem.isWindows() ? new ProcessBuilder("cmd", "/c", "cls") :
                    new ProcessBuilder("clear")).inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
