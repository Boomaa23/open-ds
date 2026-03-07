package com.boomaa.opends.util;

import com.boomaa.opends.mcp.JsonMap;
import com.boomaa.opends.mcp.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads and provides access to a button-map.json configuration file
 * that maps joystick buttons and axes to human-readable labels.
 * Used by both the virtual gamepad UI (for button labels) and the
 * MCP server (for the get_button_map tool).
 *
 * <p>The config file is searched for in the working directory as
 * {@code button-map.json}. If not found, all lookups return null
 * and {@link #getRawJson()} returns null.</p>
 *
 * <p>Example button-map.json:
 * <pre>
 * {
 *   "joysticks": {
 *     "0": {
 *       "name": "Driver Controller",
 *       "buttons": { "1": "Toggle Intake", "8": "Spin Up Shooter" },
 *       "axes": { "2": "Limelight Aim Trigger" },
 *       "pov": { "up": "Climber Up" },
 *       "notes": "To fire: button 8 then button 2"
 *     }
 *   }
 * }
 * </pre>
 */
public final class ButtonMapConfig {
    private static final String CONFIG_FILENAME = "button-map.json";
    private static JsonMap config;
    private static String rawJson;

    private ButtonMapConfig() {
    }

    /**
     * Loads the button-map.json file from the working directory.
     * Safe to call multiple times; will reload from disk each time.
     */
    public static void load() {
        File file = new File(CONFIG_FILENAME);
        if (!file.exists()) {
            Debug.println("No button-map.json found in working directory — button labels disabled");
            config = null;
            rawJson = null;
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            rawJson = sb.toString().trim();
            config = JsonParser.parseObject(rawJson);
            Debug.println("Loaded button map config from " + file.getAbsolutePath());
        } catch (IOException e) {
            Debug.println("Failed to read button-map.json: " + e.getMessage());
            config = null;
            rawJson = null;
        } catch (IllegalArgumentException e) {
            Debug.println("Failed to parse button-map.json: " + e.getMessage());
            config = null;
            rawJson = null;
        }
    }

    /** Reloads the config from disk. */
    public static void reload() {
        load();
    }

    /** Returns true if a config file was successfully loaded. */
    public static boolean isLoaded() {
        return config != null;
    }

    /**
     * Gets the label for a button on a given joystick index.
     * @param joystickIndex 0-based joystick index
     * @param button 1-based button number (matches WPILib convention)
     * @return the label string, or null if not configured
     */
    public static String getButtonLabel(int joystickIndex, int button) {
        JsonMap js = getJoystickMap(joystickIndex);
        if (js == null) {
            return null;
        }
        Object buttons = js.get("buttons");
        if (!(buttons instanceof JsonMap)) {
            return null;
        }
        Object label = ((JsonMap) buttons).get(String.valueOf(button));
        if (label instanceof String && !((String) label).isEmpty()) {
            return (String) label;
        }
        return null;
    }

    /**
     * Gets the label for an axis on a given joystick index.
     * @param joystickIndex 0-based joystick index
     * @param axis axis identifier (e.g. "0", "2", or "X", "RZ")
     * @return the label string, or null if not configured
     */
    public static String getAxisLabel(int joystickIndex, String axis) {
        JsonMap js = getJoystickMap(joystickIndex);
        if (js == null) {
            return null;
        }
        Object axes = js.get("axes");
        if (!(axes instanceof JsonMap)) {
            return null;
        }
        Object label = ((JsonMap) axes).get(axis);
        if (label instanceof String && !((String) label).isEmpty()) {
            return (String) label;
        }
        return null;
    }

    /**
     * Gets the label for a POV direction on a given joystick index.
     * @param joystickIndex 0-based joystick index
     * @param direction one of "up", "down", "left", "right"
     * @return the label string, or null if not configured
     */
    public static String getPovLabel(int joystickIndex, String direction) {
        JsonMap js = getJoystickMap(joystickIndex);
        if (js == null) {
            return null;
        }
        Object pov = js.get("pov");
        if (!(pov instanceof JsonMap)) {
            return null;
        }
        Object label = ((JsonMap) pov).get(direction);
        if (label instanceof String && !((String) label).isEmpty()) {
            return (String) label;
        }
        return null;
    }

    /**
     * Gets the notes/hints for a given joystick index.
     * @param joystickIndex 0-based joystick index
     * @return the notes string, or null if not configured
     */
    public static String getNotes(int joystickIndex) {
        JsonMap js = getJoystickMap(joystickIndex);
        if (js == null) {
            return null;
        }
        Object notes = js.get("notes");
        return notes instanceof String ? (String) notes : null;
    }

    /**
     * Gets the controller name for a given joystick index.
     * @param joystickIndex 0-based joystick index
     * @return the name string, or null if not configured
     */
    public static String getControllerName(int joystickIndex) {
        JsonMap js = getJoystickMap(joystickIndex);
        if (js == null) {
            return null;
        }
        Object name = js.get("name");
        return name instanceof String ? (String) name : null;
    }

    /**
     * Returns the raw JSON string of the entire config file,
     * or null if no config was loaded.
     */
    public static String getRawJson() {
        return rawJson;
    }

    private static JsonMap getJoystickMap(int joystickIndex) {
        if (config == null) {
            return null;
        }
        Object joysticks = config.get("joysticks");
        if (!(joysticks instanceof JsonMap)) {
            return null;
        }
        Object js = ((JsonMap) joysticks).get(String.valueOf(joystickIndex));
        if (js instanceof JsonMap) {
            return (JsonMap) js;
        }
        return null;
    }
}
