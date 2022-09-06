package com.boomaa.opends.headless;

import com.boomaa.opends.data.StatsFields;

import java.util.Scanner;

public class HeadlessController {
    public static final String LOGO_ASCII = ""
        + "   ____                   ____  _____\n"
        + "  / __ \\____  ___  ____  / __ \\/ ___/\n"
        + " / / / / __ \\/ _ \\/ __ \\/ / / /\\__ \\ \n"
        + "/ /_/ / /_/ /  __/ / / / /_/ /___/ / \n"
        + "\\____/ .___/\\___/_/ /_/_____//____/  \n"
        + "    /_/                              \n";
    private static final Scanner inputScanner = new Scanner(System.in);
    private static final ConsoleTable statusTable = new ConsoleTable();
    private static final ConsoleTable statisticsTable = new ConsoleTable();
    private static final OptionTable mainActionsTable = new OptionTable(true);
    private static final OptionTable jsActionsTable = new OptionTable(false);

    static {
        statusTable.appendRow("Voltage", "")
                .appendRow("Robot", "")
                .appendRow("Code", "")
                .appendRow("EStop", "")
                .appendRow("FMS", "")
                .appendRow("Time", "");
        for (StatsFields sf : StatsFields.values()) {
            statisticsTable.appendRow(sf.getKey(), "");
        }
        //TODO option operation runnables
        mainActionsTable.appendOption("Toggle Enable", null)
                .appendOption("Change Mode", null)
                .appendOption("Restart Robot Code", null)
                .appendOption("Restart RoboRIO", null)
                .appendOption("Emergency Stop", null)
                .appendOption("Change Alliance Number", null)
                .appendOption("Change Alliance Color", null)
                .appendOption("Change Team Number", null)
                .appendOption("Enter Game Data", null)
                .appendOption("Toggle FMS Connection", null)
                .appendOption("Toggle USB Connection", null)
                .appendOption("Change Protocol Year", null)
                .appendOption("View Statistics", null)
                .appendOption("View OpenDS Log", null)
                .appendOption("View Shuffleboard", null)
                .appendOption("Configure/Test Joysticks", null);
        //TODO joystick options table
    }

    private HeadlessController() {
    }

    public static void start() {
        printMenu();
    }

    public static void printMenu() {
        System.out.println(LOGO_ASCII
            + "by Boomaa23\n"
            + "------------------\n\n"
            + "Status: \n"
            + statusTable
            + "\n"
            + "Actions:\n"
            + mainActionsTable
        );
    }

    public static String prompt(String message) {
        System.out.println(message);
        return getInput();
    }

    public static String getInput() {
        return inputScanner.nextLine();
    }
}
