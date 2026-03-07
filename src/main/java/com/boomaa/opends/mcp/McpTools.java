package com.boomaa.opends.mcp;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.usb.Component;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.usb.VirtualController;
import com.boomaa.opends.util.ButtonMapConfig;
import com.boomaa.opends.util.Debug;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP tool definitions and implementations for reading robot status
 * and controlling virtual joystick inputs.
 */
public final class McpTools {
    /** Lazily-created virtual controllers keyed by joystick index. */
    private static final Map<Integer, VirtualController> virtualControllers = new HashMap<>();

    private McpTools() {
    }

    public static JsonList getToolDefinitions() {
        JsonList tools = new JsonList();
        tools.add(defineTool("get_robot_status",
            "Get the current robot status including enabled state, operating mode, "
                + "estop status, robot code status, and battery voltage.",
            emptySchema()));

        tools.add(defineTool("get_battery_voltage",
            "Get the current robot battery voltage reading.",
            emptySchema()));

        tools.add(defineTool("get_robot_stats",
            "Get detailed robot statistics including disk space, RAM, CPU usage, "
                + "CAN bus metrics, RoboRIO and WPILib versions, and fault information.",
            emptySchema()));

        tools.add(defineTool("get_connection_info",
            "Get connection status for the robot (RoboRIO) and the FMS (Field Management System).",
            emptySchema()));

        tools.add(defineTool("get_match_info",
            "Get match-related information including alliance station color and number, "
                + "protocol year, match time, and game data.",
            emptySchema()));

        tools.add(defineTool("get_button_map",
            "Get the button/axis mapping configuration for all joysticks. Returns "
                + "human-readable labels describing what each button and axis does "
                + "on the connected robot. Call this first to understand the controls "
                + "before using press_button or set_axis.",
            emptySchema()));

        tools.add(defineTool("press_button",
            "Press or release a button on a virtual joystick. The button is sent to "
                + "the robot just like a real USB controller. Use get_button_map first "
                + "to learn what each button does.",
            buttonSchema()));

        tools.add(defineTool("set_axis",
            "Set an axis value on a virtual joystick. The value is sent to the robot "
                + "just like a real USB controller. Valid axes: X, Y (left stick), "
                + "Z, RY (right stick), RX (right trigger), RZ (left trigger).",
            axisSchema()));

        return tools;
    }

    public static JsonMap callTool(String name, JsonMap arguments) {
        switch (name) {
            case "get_robot_status":
                return wrapTextResult(getRobotStatus());
            case "get_battery_voltage":
                return wrapTextResult(getBatteryVoltage());
            case "get_robot_stats":
                return wrapTextResult(getRobotStats());
            case "get_connection_info":
                return wrapTextResult(getConnectionInfo());
            case "get_match_info":
                return wrapTextResult(getMatchInfo());
            case "get_button_map":
                return wrapTextResult(getButtonMap());
            case "press_button":
                return pressButton(arguments);
            case "set_axis":
                return setAxisValue(arguments);
            default:
                return wrapErrorResult("Unknown tool: " + name);
        }
    }

    private static String getRobotStatus() {
        JsonMap status = new JsonMap();

        boolean robotConnected = DisplayEndpoint.NET_IF_INIT.isInit(Remote.ROBO_RIO);
        status.put("robotConnected", robotConnected);
        status.put("enabled", MainJDEC.IS_ENABLED.isSelected());
        status.put("mode", String.valueOf(MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem()));
        status.put("estop", MainJDEC.ESTOP_STATUS.isDisplayed());
        status.put("batteryVoltage", MainJDEC.BAT_VOLTAGE.getText());

        boolean codeRunning = MainJDEC.ROBOT_CODE_STATUS.isDisplayed();
        status.put("robotCodeRunning", codeRunning);

        String codeStatus = "Not Running";
        if (MainJDEC.ROBOT_CODE_STATUS.isDisplayed()) {
            codeStatus = MainJDEC.ROBOT_CODE_STATUS.getText();
        }
        status.put("robotCodeStatus", codeStatus);

        return status.toJson();
    }

    private static String getBatteryVoltage() {
        JsonMap voltage = new JsonMap();
        String voltText = MainJDEC.BAT_VOLTAGE.getText();
        voltage.put("voltage", voltText);
        try {
            String numericPart = voltText.replace(" V", "").trim();
            voltage.put("volts", Double.parseDouble(numericPart));
        } catch (NumberFormatException e) {
            voltage.put("volts", 0.0);
        }
        return voltage.toJson();
    }

    private static String getRobotStats() {
        JsonMap stats = new JsonMap();

        JsonMap roborio = new JsonMap();
        roborio.put("diskSpace", StatsFields.DISK_SPACE.getValue());
        roborio.put("ramSpace", StatsFields.RAM_SPACE.getValue());
        roborio.put("cpuPercent", StatsFields.CPU_PERCENT.getValue());
        roborio.put("rioVersion", StatsFields.ROBORIO_VERSION.getValue());
        roborio.put("wpilibVersion", StatsFields.WPILIB_VERSION.getValue());
        stats.put("roborio", roborio);

        JsonMap canBus = new JsonMap();
        canBus.put("utilization", StatsFields.CAN_UTILIZATION.getValue());
        canBus.put("busOff", StatsFields.CAN_BUS_OFF.getValue());
        canBus.put("txFull", StatsFields.CAN_TX_FULL.getValue());
        canBus.put("rxError", StatsFields.CAN_RX_ERR.getValue());
        canBus.put("txError", StatsFields.CAN_TX_ERR.getValue());
        stats.put("canBus", canBus);

        JsonMap disableFaults = new JsonMap();
        disableFaults.put("comms", StatsFields.DISABLE_FAULTS_COMMS.getValue());
        disableFaults.put("twelveVolt", StatsFields.DISABLE_FAULTS_12V.getValue());
        stats.put("disableFaults", disableFaults);

        JsonMap railFaults = new JsonMap();
        railFaults.put("sixVolt", StatsFields.RAIL_FAULTS_6V.getValue());
        railFaults.put("fiveVolt", StatsFields.RAIL_FAULTS_5V.getValue());
        railFaults.put("threePointThreeVolt", StatsFields.RAIL_FAULTS_3P3V.getValue());
        stats.put("railFaults", railFaults);

        return stats.toJson();
    }

    private static String getConnectionInfo() {
        JsonMap info = new JsonMap();

        boolean robotConnected = DisplayEndpoint.NET_IF_INIT.isInit(Remote.ROBO_RIO);
        info.put("robotConnected", robotConnected);

        boolean robotSimulated = MainJDEC.ROBOT_CONNECTION_STATUS.isDisplayed()
            && "Simulated".equals(MainJDEC.ROBOT_CONNECTION_STATUS.getText());
        info.put("robotSimulated", robotSimulated);

        boolean fmsConnected = MainJDEC.FMS_CONNECTION_STATUS.isDisplayed();
        info.put("fmsConnected", fmsConnected);

        boolean fmsEnabled = MainJDEC.FMS_CONNECT.isSelected();
        info.put("fmsEnabled", fmsEnabled);

        boolean usbEnabled = MainJDEC.USB_CONNECT.isSelected();
        info.put("usbConnection", usbEnabled);

        return info.toJson();
    }

    private static String getMatchInfo() {
        JsonMap info = new JsonMap();
        info.put("allianceColor", String.valueOf(MainJDEC.ALLIANCE_COLOR.getSelectedItem()));
        info.put("allianceNumber", String.valueOf(MainJDEC.ALLIANCE_NUM.getSelectedItem()));
        info.put("protocolYear", MainJDEC.getProtocolYear());
        info.put("teamNumber", MainJDEC.TEAM_NUMBER.getText());
        info.put("gameData", MainJDEC.GAME_DATA.getText());

        String matchTime = MainJDEC.MATCH_TIME.getText();
        boolean matchTimeVisible = MainJDEC.MATCH_TIME.isDisplayed();
        info.put("matchTime", matchTimeVisible ? matchTime : "N/A");
        info.put("matchTimeActive", matchTimeVisible);

        return info.toJson();
    }

    // ---- Button map config ----

    private static String getButtonMap() {
        String raw = ButtonMapConfig.getRawJson();
        if (raw == null) {
            JsonMap result = new JsonMap();
            result.put("error", "No button-map.json found in working directory");
            result.put("hint", "Create a button-map.json file to map joystick buttons to robot actions");
            return result.toJson();
        }
        return raw;
    }

    // ---- Virtual joystick control ----

    private static VirtualController getOrCreateVirtualController(int joystickIndex) {
        VirtualController ctrl = virtualControllers.get(joystickIndex);
        if (ctrl == null) {
            ctrl = new VirtualController();
            HIDDevice device = new HIDDevice(ctrl);
            // Force the device to the requested joystick index
            if (device.getIdx() != joystickIndex) {
                device.setIdx(joystickIndex);
            }
            ControlDevices.getAll().put(joystickIndex, device);
            virtualControllers.put(joystickIndex, ctrl);
            Debug.println("MCP: Created virtual controller at joystick index " + joystickIndex);
        }
        return ctrl;
    }

    private static JsonMap pressButton(JsonMap arguments) {
        if (arguments == null) {
            return wrapErrorResult("Missing arguments");
        }
        Object jsObj = arguments.get("joystick");
        Object btnObj = arguments.get("button");
        Object pressedObj = arguments.get("pressed");

        if (jsObj == null || btnObj == null || pressedObj == null) {
            return wrapErrorResult("Required: joystick (int 0-5), button (int 1+), pressed (bool)");
        }

        int joystick = ((Number) jsObj).intValue();
        int button = ((Number) btnObj).intValue();
        boolean pressed = Boolean.TRUE.equals(pressedObj);

        if (joystick < 0 || joystick > 5) {
            return wrapErrorResult("joystick must be 0-5, got " + joystick);
        }
        if (button < 1 || button > 10) {
            return wrapErrorResult("button must be 1-10, got " + button);
        }

        VirtualController ctrl = getOrCreateVirtualController(joystick);
        ctrl.setButton(button - 1, pressed);

        JsonMap result = new JsonMap();
        result.put("joystick", joystick);
        result.put("button", button);
        result.put("pressed", pressed);

        String label = ButtonMapConfig.getButtonLabel(joystick, button);
        if (label != null) {
            result.put("label", label);
        }
        result.put("status", "ok");
        Debug.println("MCP: Button " + button + " on joystick " + joystick
            + (pressed ? " pressed" : " released")
            + (label != null ? " (" + label + ")" : ""));
        return wrapTextResult(result.toJson());
    }

    private static JsonMap setAxisValue(JsonMap arguments) {
        if (arguments == null) {
            return wrapErrorResult("Missing arguments");
        }
        Object jsObj = arguments.get("joystick");
        Object axisObj = arguments.get("axis");
        Object valueObj = arguments.get("value");

        if (jsObj == null || axisObj == null || valueObj == null) {
            return wrapErrorResult("Required: joystick (int 0-5), axis (X/Y/Z/RX/RY/RZ), value (number -1.0 to 1.0)");
        }

        int joystick = ((Number) jsObj).intValue();
        String axisName = axisObj.toString();
        double value = ((Number) valueObj).doubleValue();

        if (joystick < 0 || joystick > 5) {
            return wrapErrorResult("joystick must be 0-5, got " + joystick);
        }
        if (value < -1.0 || value > 1.0) {
            return wrapErrorResult("value must be -1.0 to 1.0, got " + value);
        }

        Component.Axis axis;
        try {
            axis = Component.Axis.valueOf(axisName);
        } catch (IllegalArgumentException e) {
            return wrapErrorResult("Invalid axis: " + axisName + ". Must be one of: X, Y, Z, RX, RY, RZ");
        }

        VirtualController ctrl = getOrCreateVirtualController(joystick);
        ctrl.setAxis(axis, value);

        JsonMap result = new JsonMap();
        result.put("joystick", joystick);
        result.put("axis", axisName);
        result.put("value", value);
        result.put("status", "ok");
        Debug.println("MCP: Axis " + axisName + " on joystick " + joystick + " set to " + value);
        return wrapTextResult(result.toJson());
    }

    // ---- Tool schema builders ----

    private static JsonMap buttonSchema() {
        JsonMap schema = new JsonMap();
        schema.put("type", "object");

        JsonMap properties = new JsonMap();

        JsonMap jsProp = new JsonMap();
        jsProp.put("type", "integer");
        jsProp.put("description", "Joystick index (0-5)");
        properties.put("joystick", jsProp);

        JsonMap btnProp = new JsonMap();
        btnProp.put("type", "integer");
        btnProp.put("description", "Button number (1-based, matching WPILib convention)");
        properties.put("button", btnProp);

        JsonMap pressedProp = new JsonMap();
        pressedProp.put("type", "boolean");
        pressedProp.put("description", "true to press, false to release");
        properties.put("pressed", pressedProp);

        schema.put("properties", properties);

        JsonList required = new JsonList();
        required.add("joystick");
        required.add("button");
        required.add("pressed");
        schema.put("required", required);

        return schema;
    }

    private static JsonMap axisSchema() {
        JsonMap schema = new JsonMap();
        schema.put("type", "object");

        JsonMap properties = new JsonMap();

        JsonMap jsProp = new JsonMap();
        jsProp.put("type", "integer");
        jsProp.put("description", "Joystick index (0-5)");
        properties.put("joystick", jsProp);

        JsonMap axisProp = new JsonMap();
        axisProp.put("type", "string");
        axisProp.put("description", "Axis name: X, Y (left stick), Z, RY (right stick), RX (right trigger), RZ (left trigger)");
        properties.put("axis", axisProp);

        JsonMap valueProp = new JsonMap();
        valueProp.put("type", "number");
        valueProp.put("description", "Axis value from -1.0 to 1.0");
        properties.put("value", valueProp);

        schema.put("properties", properties);

        JsonList required = new JsonList();
        required.add("joystick");
        required.add("axis");
        required.add("value");
        schema.put("required", required);

        return schema;
    }

    private static JsonMap defineTool(String name, String description, JsonMap inputSchema) {
        JsonMap tool = new JsonMap();
        tool.put("name", name);
        tool.put("description", description);
        tool.put("inputSchema", inputSchema);
        return tool;
    }

    private static JsonMap emptySchema() {
        JsonMap schema = new JsonMap();
        schema.put("type", "object");
        schema.put("properties", new JsonMap());
        return schema;
    }

    private static JsonMap wrapTextResult(String text) {
        JsonMap content = new JsonMap();
        content.put("type", "text");
        content.put("text", text);

        JsonList contentList = new JsonList();
        contentList.add(content);

        JsonMap result = new JsonMap();
        result.put("content", contentList);
        result.put("isError", false);
        return result;
    }

    private static JsonMap wrapErrorResult(String message) {
        JsonMap content = new JsonMap();
        content.put("type", "text");
        content.put("text", message);

        JsonList contentList = new JsonList();
        contentList.add(content);

        JsonMap result = new JsonMap();
        result.put("content", contentList);
        result.put("isError", true);
        return result;
    }
}
