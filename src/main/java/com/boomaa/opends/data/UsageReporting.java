package com.boomaa.opends.data;

import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.util.ArrayUtils;

import java.util.HashMap;

public class UsageReporting {
    public static byte[] RECEIVED_USAGE = null;

    public static TagValueMap<String> decode(byte[] bytes, int size) {
        RECEIVED_USAGE = bytes;
        TagValueMap<String> map = new TagValueMap<>();
        map.addTo("Team Num", new String(ArrayUtils.sliceArr(bytes, 0, 2)));
        char[] asChars = new String(ArrayUtils.sliceArr(bytes, 3)).toCharArray();
        String numAssoc = null;
        Mapping mapping = null;
        int namingCounter = 0;
        for (int i = 0; i < asChars.length; i++) {
            if (asChars[i] == '>' && i + 1 < asChars.length) {
                mapping = KeyMap.getInstance().get(String.valueOf(asChars[i]) + asChars[++i]);
            } else if (Character.isAlphabetic(asChars[i])) {
                mapping = KeyMap.getInstance().get(String.valueOf(asChars[i]));
            } else {
                try {
                    Integer.parseInt(String.valueOf(asChars[i]));
                    numAssoc = String.valueOf(asChars[i]);
                    if (i + 1 < asChars.length) {
                        Integer.parseInt(String.valueOf(asChars[i + 1]));
                        numAssoc += String.valueOf(asChars[++i]);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            if (numAssoc != null && mapping != null) {
                String concatStr = numAssoc;
                switch (mapping.getIdPrefix()) {
                    case LANG_TYPE:
                        switch (Integer.parseInt(concatStr)) {
                            case 1: concatStr = "LabVIEW"; break;
                            case 2: concatStr = "C++"; break;
                            case 3: concatStr = "Java"; break;
                            case 4: concatStr = "Python"; break;
                            case 5: concatStr = ".NET"; break;
                        }
                        break;
                    case ADXL345:
                        switch (Integer.parseInt(concatStr)) {
                            case 1: concatStr = "SPI"; break;
                            case 2: concatStr = "I2C"; break;
                        }
                        break;
                    case FRAMEWORK_TYPE:
                        switch (Integer.parseInt(concatStr)) {
                            case 1: concatStr = "Iterative"; break;
                            case 2: concatStr = "Simple"; break;
                            case 3: concatStr = "CommandControl"; break;
                        }
                        break;
                    case SPI_PORT:
                        switch (Integer.parseInt(concatStr)) {
                            case 0: concatStr = "OnboardCS0"; break;
                            case 1: concatStr = "OnboardCS1"; break;
                            case 2: concatStr = "OnboardCS2"; break;
                            case 3: concatStr = "OnboardCS3"; break;
                            case 4: concatStr = "MXP"; break;
                        }
                        break;
                }
                String prefix = mapping.idPrefix.getPrefix();
                System.out.println(mapping.getName() + " (" + prefix + (prefix.length() != 0 ? " " : "") + concatStr + ")");
                map.addTo("Usage Report " + namingCounter++, mapping.getName() + " (" + prefix + (prefix.length() != 0 ? " " : "") + concatStr + ")");
                mapping = null;
                numAssoc = null;
            }

        }
        return map;
    }

    public static class KeyMap extends HashMap<String, Mapping> {
        private static KeyMap INSTANCE;

        private KeyMap() {
            addTriple(
                    new String[] {
                            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                            ">A", ">B", ">C", ">D", ">E", ">F", ">G", ">H", ">I", ">J", ">K", ">L", ">M", ">N", ">O", ">P", ">Q", ">R", ">S"
                    }, new String[] {
                            "Controller", "Module", "Language", "CANPlugin", "Accelerometer", "ADXL345", "AnalogChannel", "AnalogTrigger", "AnalogTriggerOutput",
                            "CANJaguar", "Compressor", "Counter", "Dashboard", "DigitalInput", "DigitalOutput", "DriverStationCIO", "DriverStationEIO", "DriverStationLCD",
                            "Encoder", "GearTooth", "Gyro", "I2C", "Framework", "Jaguar", "Joystick", "Kinect",
                            "KinectStick", "PIDController", "Preferences", "PWM", "Relay", "RobotDrive", "SerialPort", "Servo", "Solenoid", "SPI", "Task", "Ultrasonic",
                            "Victor", "Button", "Command", "AxisCamera", "PCVideoServer", "SmartDashboard", "Talon", "HiTechnicColorSensor", "HiTechnicAccel",
                            "HiTechnicCompass", "SRF08 AnalogOutput", "SRF08 AnalogOutput", "VictorSP PWMTalonSRX", "VictorSP PWMTalonSRX",
                            "CANTalonSRX", "ADXL362", "ADXRS450", "RevSPARK", "MindsensorsSD540", "DigitalFilter", "ADIS16448", "PDP", "PCM", "PigeonIMU",
                            "NidecBrushless", "CANifier", "CTRE_future0", "CTRE_future1", "CTRE_future2", "CTRE_future3", "CTRE_future4", "CTRE_future5", "CTRE_future6"
                    }, new IdPrefix[] {
                            null, null, IdPrefix.LANG_TYPE, null, IdPrefix.CHANNEL, IdPrefix.ADXL345, IdPrefix.CHANNEL, IdPrefix.CHANNEL, IdPrefix.TRIGGER_INDEX,
                            null, IdPrefix.PCM_ID, IdPrefix.INDEX, null, IdPrefix.CHANNEL, IdPrefix.CHANNEL, null, null, null, IdPrefix.FPGA_INDEX, IdPrefix.CHANNEL,
                            IdPrefix.CHANNEL, IdPrefix.DEVICE_ADDRESS, IdPrefix.FRAMEWORK_TYPE, IdPrefix.CHANNEL, IdPrefix.PORT, null, null, IdPrefix.INSTANCE_NUM,
                            IdPrefix.ZERO, IdPrefix.CHANNEL, IdPrefix.CHANNEL, IdPrefix.NUM_MOTORS, IdPrefix.ZERO, IdPrefix.CHANNEL, IdPrefix.CHANNEL, IdPrefix.INSTANCE_NUM,
                            null, IdPrefix.CHANNEL, IdPrefix.CHANNEL, null, null, IdPrefix.HANDLE, IdPrefix.HANDLE, IdPrefix.ZERO, IdPrefix.CHANNEL, null, null, null,
                            IdPrefix.CHANNEL, IdPrefix.CHANNEL, IdPrefix.CHANNEL, IdPrefix.CHANNEL, IdPrefix.DEVICE_ID, IdPrefix.SPI_PORT, IdPrefix.SPI_PORT, IdPrefix.CHANNEL,
                            IdPrefix.CHANNEL, IdPrefix.CHANNEL, null, null, null, IdPrefix.DEVICE_ID, IdPrefix.CHANNEL, IdPrefix.DEVICE_ID, IdPrefix.TALON_DEVICE_ID,
                            IdPrefix.DEVICE_ID, IdPrefix.DEVICE_ID, IdPrefix.DEVICE_ID, null, null, null
                    }
            );
            INSTANCE = this;
        }

        private void addTriple(String[] keys, String[] names, IdPrefix[] idPrefixes) {
            for (int i = 0; i < keys.length; i++) {
                super.put(keys[i], new Mapping(keys[i], names[i], idPrefixes[i] != null ? idPrefixes[i] : IdPrefix.NONE));
            }
        }

        public static KeyMap getInstance() {
            return INSTANCE == null ? new KeyMap() : INSTANCE;
        }
    }

    public static class Mapping {
        private final String key;
        private final String name;
        private final IdPrefix idPrefix;

        public Mapping(String key, String name, IdPrefix idPrefix) {
            this.key = key;
            this.name = name;
            this.idPrefix = idPrefix;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public IdPrefix getIdPrefix() {
            return idPrefix;
        }
    }

    public enum IdPrefix {
        NONE(""),
        ZERO(""),
        LANG_TYPE(""),
        CHANNEL("Channel"),
        ADXL345(""),
        TRIGGER_INDEX("Trigger Index"),
        PCM_ID("PCM ID"),
        INDEX("Index"),
        FPGA_INDEX("FPGA Index"),
        DEVICE_ADDRESS("Device Address"),
        FRAMEWORK_TYPE(""),
        PORT("Port"),
        INSTANCE_NUM("Instance"),
        NUM_MOTORS("Number of Motors"),
        HANDLE("Handle"),
        DEVICE_ID("Device ID"),
        TALON_DEVICE_ID("Talon Device ID"),
        SPI_PORT("SPI Port");

        private final String prefix;

        IdPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
