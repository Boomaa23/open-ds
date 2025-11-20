package com.boomaa.opends.headless;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.Logger;
import com.boomaa.opends.display.MainJDEC;

import java.util.function.Supplier;

public enum MainHAction implements HeadlessJDEC {
    TOGGLE_ENABLE("Toggle Enable",
        () -> {
            if (MainJDEC.IS_ENABLED.isEnabled()) {
                MainJDEC.IS_ENABLED.setSelected(!MainJDEC.IS_ENABLED.isSelected());
                return OperationReturn.CONTINUE;
            } else {
                System.err.println("Robot cannot be enabled.");
                return OperationReturn.INVALID;
            }
        },
        () -> MainJDEC.IS_ENABLED.isSelected() ? "Enabled" : "Disabled"
    ),
    CHANGE_MODE("Change Mode",
        () -> {
            System.out.println(CHANGE_MODE_TABLE);
            return HeadlessController.runOperationFromTable(CHANGE_MODE_TABLE);
        },
        () -> MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem().toString()
    ),
    RESTART_ROBOT_CODE("Restart Robot Code",
        () -> {
            MainJDEC.RESTART_CODE_BTN.doClick();
            return OperationReturn.CONTINUE;
        }
    ),
    RESTART_ROBO_RIO("Restart RoboRIO",
        () -> {
            MainJDEC.RESTART_ROBO_RIO_BTN.doClick();
            return OperationReturn.CONTINUE;
        }
    ),
    EMERGENCY_STOP("Emergency Stop",
        () -> {
            MainJDEC.ESTOP_BTN.doClick();
            return OperationReturn.CONTINUE;
        }
    ),
    CHANGE_ALLIANCE_NUMBER("Change Alliance Number",
        () -> {
            System.out.println(CHANGE_ANUM_TABLE);
            return HeadlessController.runOperationFromTable(CHANGE_ANUM_TABLE);
        },
        () -> MainJDEC.ALLIANCE_NUM.getSelectedItem().toString()
    ),
    CHANGE_ALLIANCE_COLOR("Change Alliance Color",
        () -> {
            System.out.println(CHANGE_ACOLOR_TABLE);
            return HeadlessController.runOperationFromTable(CHANGE_ACOLOR_TABLE);
        },
        () -> MainJDEC.ALLIANCE_COLOR.getSelectedItem().toString()
    ),
    CHANGE_TEAM_NUMBER("Change Team Number",
        () -> {
            MainJDEC.TEAM_NUMBER.setText(HeadlessController.prompt("Enter new team number: "));
            return OperationReturn.CONTINUE;
        },
        MainJDEC.TEAM_NUMBER::getText
    ),
    ENTER_GAME_DATA("Enter Game Data",
        () -> {
            MainJDEC.GAME_DATA.setText(HeadlessController.prompt("Enter new game data: "));
            return OperationReturn.CONTINUE;
        },
        MainJDEC.GAME_DATA::getText
    ),
    TOGGLE_FMS_CONNECTION("Toggle FMS Connection",
        () -> {
            MainJDEC.FMS_CONNECT.setSelected(true);
            return OperationReturn.CONTINUE;
        }
    ),
    TOGGLE_USB_CONNECTION("Toggle USB Connection",
        () -> {
            MainJDEC.USB_CONNECT.setSelected(true);
            return OperationReturn.CONTINUE;
        }
    ),
    CHANGE_PROTOCOL_YEAR("Change Protocol Year",
        () -> {
            String response = HeadlessController.prompt("Enter new team number: ");
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
        () -> MainJDEC.PROTOCOL_YEAR.getSelectedItem().toString()
    ),
    VIEW_STATISTICS("View Statistics",
        () -> {
            System.out.println(STATISTICS_TABLE);
            return OperationReturn.WAIT;
        }
    ),
    VIEW_OPENDS_LOG("View OpenDS Log",
        () -> {
            System.out.println(Logger.LOGGER.getAppender().getTextArea().getText());
            return OperationReturn.WAIT;
        }
    ),
    VIEW_SHUFFLEBOARD("View Shuffleboard",
        () -> {
            System.out.println(SHUFFLEBOARD_TABLE);
            return OperationReturn.WAIT;
        }
    ), //TODO
    CONFIG_JOYSTICKS("Configure/Test Joysticks",
        () -> {
            System.out.println(JS_ACTIONS_TABLE);
            return HeadlessController.runOperationFromTable(JS_ACTIONS_TABLE);
        }
    ), //TODO
    REFRESH_DISPLAY("Refresh Display",
            () -> OperationReturn.CONTINUE
    ),
    QUIT("Quit",
        () -> {
            System.exit(0);
            return OperationReturn.INVALID;
        }
    );

    private final String optionName;
    private final Supplier<OperationReturn> operation;
    private final Supplier<String> supplier;

    MainHAction(String optionName, Supplier<OperationReturn> operation, Supplier<String> supplier) {
        this.optionName = optionName;
        this.operation = operation;
        this.supplier = supplier;
    }

    MainHAction(String optionName, Supplier<OperationReturn> operation) {
        this(optionName, operation, null);
    }

    public String getOptionName() {
        return optionName;
    }

    public Supplier<OperationReturn> getOperation() {
        return operation;
    }

    public Supplier<String> getSupplier() {
        return supplier;
    }
}
