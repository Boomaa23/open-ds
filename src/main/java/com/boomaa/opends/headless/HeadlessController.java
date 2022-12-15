package com.boomaa.opends.headless;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.Logger;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.util.OperatingSystem;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Supplier;

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
    private static final OptionTable mainActionsTable = new OptionTable(18, true, false);
    private static final OptionTable jsActionsTable = new OptionTable(18, true, false); //TODO
    private static final ConsoleTable shuffleboardTable = new ConsoleTable(7, 2); //TODO

    private static final OptionTable changeModeTable = new OptionTable(RobotMode.values().length + 1, false, false);
    private static final OptionTable changeANumTable = new OptionTable(MainJDEC.ALLIANCE_NUM.getItemCount() + 1, false, false);
    private static final OptionTable changeAColorTable = new OptionTable(MainJDEC.ALLIANCE_COLOR.getItemCount() + 1, false, false);

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

        for (RobotMode mode : RobotMode.values()) {
            changeModeTable.appendOption(mode.toString(), () -> {
                MainJDEC.ROBOT_DRIVE_MODE.setSelectedItem(mode);
                return OperationReturn.CONTINUE;
            });
        }
        changeModeTable.appendOption("(go back)", () -> OperationReturn.CONTINUE);

        for (int i = 1; i < MainJDEC.ALLIANCE_NUM.getItemCount() + 1; i++) {
            int finalI = i;
            changeANumTable.appendOption(String.valueOf(i), () -> {
                MainJDEC.ALLIANCE_NUM.setSelectedItem(finalI);
                return OperationReturn.CONTINUE;
            });
        }
        changeANumTable.appendOption("(go back)", () -> OperationReturn.CONTINUE);

        for (int i = 0; i < MainJDEC.ALLIANCE_COLOR.getItemCount(); i++) {
            String item = MainJDEC.ALLIANCE_COLOR.getItems()[i];
            changeAColorTable.appendOption(item, () -> {
                MainJDEC.ALLIANCE_COLOR.setSelectedItem(item);
                return OperationReturn.CONTINUE;
            });
        }
        changeAColorTable.appendOption("(go back)", () -> OperationReturn.CONTINUE);


        //TODO option operation runnables
        mainActionsTable.appendOption("Toggle Enable",
                    () -> {
                        if (MainJDEC.IS_ENABLED.isEnabled()) {
                            MainJDEC.IS_ENABLED.setSelected(!MainJDEC.IS_ENABLED.isSelected());
                            return OperationReturn.CONTINUE;
                        } else {
                            System.err.println("Robot cannot be enabled.");
                            return OperationReturn.INVALID;
                        }
                     },
                    () -> MainJDEC.IS_ENABLED.isSelected() ? "Enabled" : "Disabled")
            .appendOption("Change Mode",
                    () -> {
                        System.out.println(changeModeTable);
                        return runOperation(changeModeTable);
                    },
                    () -> MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem().toString())
            .appendOption("Restart Robot Code",
                    () -> {
                        MainJDEC.RESTART_CODE_BTN.doClick();
                        return OperationReturn.CONTINUE;
                    })
            .appendOption("Restart RoboRIO",
                    () -> {
                        MainJDEC.RESTART_ROBO_RIO_BTN.doClick();
                        return OperationReturn.CONTINUE;
                    })
            .appendOption("Emergency Stop",
                    () -> {
                        MainJDEC.ESTOP_BTN.doClick();
                        return OperationReturn.CONTINUE;
                    })
            .appendOption("Change Alliance Number",
                    () -> {
                        System.out.println(changeANumTable);
                        return runOperation(changeANumTable);
                    },
                    () -> MainJDEC.ALLIANCE_NUM.getSelectedItem().toString())
            .appendOption("Change Alliance Color",
                    () -> {
                        System.out.println(changeAColorTable);
                        return runOperation(changeAColorTable);
                    },
                    () -> MainJDEC.ALLIANCE_COLOR.getSelectedItem().toString())
            .appendOption("Change Team Number",
                    () -> {
                        MainJDEC.TEAM_NUMBER.setText(prompt("Enter new team number: "));
                        return OperationReturn.CONTINUE;
                    },
                    () -> MainJDEC.TEAM_NUMBER.getText())
            .appendOption("Enter Game Data",
                    () -> {
                        MainJDEC.GAME_DATA.setText(prompt("Enter new game data: "));
                        return OperationReturn.CONTINUE;
                    },
                    () -> MainJDEC.GAME_DATA.getText())
            .appendOption("Toggle FMS Connection",
                    () -> {
                        MainJDEC.FMS_CONNECT.setSelected(true);
                        return OperationReturn.CONTINUE;
                    })
            .appendOption("Toggle USB Connection",
                    () -> {
                        MainJDEC.USB_CONNECT.setSelected(true);
                        return OperationReturn.CONTINUE;
                    })
            .appendOption("Change Protocol Year",
                    () -> {
                        String response = prompt("Enter new team number: ");
                        try {
                            int year = Integer.parseInt(response);
                            boolean valid = false;
                            for (int validYear : DisplayEndpoint.VALID_PROTOCOL_YEARS) {
                                if (year == validYear) {
                                    valid = true;
                                    break;
                                }
                            }
                            if (valid) {
                                MainJDEC.PROTOCOL_YEAR.setSelectedItem(year);
                                return OperationReturn.CONTINUE;
                            } else {
                                System.err.println("Invalid protocol year entered.");
                                return OperationReturn.INVALID;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Protocol year entered could not be parsed.");
                            return OperationReturn.INVALID;
                        }
                    },
                    () -> MainJDEC.PROTOCOL_YEAR.getSelectedItem().toString())
            .appendOption("View Statistics",
                    () -> {
                        System.out.println(statisticsTable);
                        return OperationReturn.WAIT;
                    })
            .appendOption("View OpenDS Log",
                    () -> {
                        System.out.println(Logger.LOGGER.getAppender().getValues());
                        return OperationReturn.WAIT;
                    }) //TODO
            .appendOption("View Shuffleboard",
                    () -> {
                        System.out.println(shuffleboardTable);
                        return OperationReturn.WAIT;
                    }) //TODO
            .appendOption("Configure/Test Joysticks",
                    () -> {
                        System.out.println(jsActionsTable);
                        return runOperation(jsActionsTable);
                    }) //TODO
            .appendOption("Quit",
                    () -> {
                        System.exit(0);
                        return OperationReturn.CONTINUE;
                    });
    }

    private HeadlessController() {
    }

    public static void start() {
        printMenu();
        while (true) {
            OperationReturn retval = runOperation(mainActionsTable);
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

    private static OperationReturn runOperation(OptionTable table) {
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
        statusTable.updateAll();
        mainActionsTable.updateAll();
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
