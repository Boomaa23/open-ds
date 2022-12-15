package com.boomaa.opends.headless;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.util.OperatingSystem;

import java.io.IOException;
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
    private static final ConsoleTable statusTable = new ConsoleTable(7, 2);
    private static final ConsoleTable statisticsTable = new ConsoleTable(StatsFields.values().length + 1, 2);
    private static final OptionTable mainActionsTable = new OptionTable(18, false, false);
    private static final OptionTable jsActionsTable = new OptionTable(0, false, false);

    static {
        statusTable.setRow(0, 0, "Key", "Value");
        statusTable.setCol(1, 0, "Voltage", "Robot", "Code", "EStop", "FMS", "Time");
        statusTable.setCol(1, 1,
            MainJDEC.BAT_VOLTAGE::getText,
            MainJDEC.ROBOT_CONNECTION_STATUS::getText,
            MainJDEC.ROBOT_CODE_STATUS::getText,
            MainJDEC.ESTOP_STATUS::getText,
            MainJDEC.FMS_CONNECTION_STATUS::getText,
            MainJDEC.MATCH_TIME::getText
        );

        statisticsTable.setRow(0, 0, "Key", "Value");
        for (int i = 0; i < StatsFields.values().length; i++) {
            statisticsTable.getEntry(i + 1, 0).setValue(StatsFields.values()[i].getKey());
        }
        //TODO option operation runnables
        mainActionsTable.appendOption("Toggle Enable", () -> MainJDEC.IS_ENABLED.setSelected(true))
                .appendOption("Change Mode", NullOperation.getInstance()) //TODO
                .appendOption("Restart Robot Code", MainJDEC.RESTART_CODE_BTN::doClick)
                .appendOption("Restart RoboRIO", MainJDEC.RESTART_ROBO_RIO_BTN::doClick)
                .appendOption("Emergency Stop", MainJDEC.ESTOP_BTN::doClick)
                .appendOption("Change Alliance Number", NullOperation.getInstance()) //TODO
                .appendOption("Change Alliance Color", NullOperation.getInstance()) //TODO
                .appendOption("Change Team Number", NullOperation.getInstance()) //TODO
                .appendOption("Enter Game Data", NullOperation.getInstance()) //TODO
                .appendOption("Toggle FMS Connection", () -> MainJDEC.FMS_CONNECT.setSelected(true))
                .appendOption("Toggle USB Connection", () -> MainJDEC.USB_CONNECT.setSelected(true))
                .appendOption("Change Protocol Year", NullOperation.getInstance())
                .appendOption("View Statistics", () -> System.out.println(statisticsTable))
                .appendOption("View OpenDS Log", NullOperation.getInstance()) //TODO
                .appendOption("View Shuffleboard", NullOperation.getInstance()) //TODO
                .appendOption("Configure/Test Joysticks", NullOperation.getInstance()) //TODO
                .appendOption("Quit", () -> System.exit(0));
        //TODO joystick options table
    }

    private HeadlessController() {
    }

    public static void start() {

        printMenu();
        while (true) {
            String response = prompt("Select an action: ");
            if (response.length() != 1) {
                System.err.println("Input was too long. Please try again.");
                continue;
            }
            if (!mainActionsTable.runOperation(response.charAt(0))) {
                System.err.println("Invalid input keycode. Please try again.");
                continue;
            }
            clear();

            response = "";
            while (!response.equals("Y") && !response.equals("n")) {
                response = prompt("Return to menu? [Y/n]: ");
            }
            clear();
            printMenu();
        }
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
