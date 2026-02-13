package com.boomaa.opends.mcp;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;

/**
 * MCP tool definitions and implementations for reading robot status.
 * Provides read-only access to robot state, connection info, and diagnostics.
 */
public final class McpTools {
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
