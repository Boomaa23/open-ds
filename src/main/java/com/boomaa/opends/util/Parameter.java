package com.boomaa.opends.util;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import java.util.Arrays;
import java.util.List;

public enum Parameter {
    ALLIANCE_COLOR("--alliance-color", Type.STRING, MainJDEC.ALLIANCE_COLOR, "Red", "Blue"),
    ALLIANCE_NUMBER("--alliance-num", Type.INTEGER,  MainJDEC.ALLIANCE_NUM, 1, 2, 3),
    FMS("--fms", Type.BOOLEAN,  MainJDEC.FMS_CONNECT),
    GAME_DATA("--game-data", Type.STRING,  MainJDEC.GAME_DATA),
    PROTOCOL_YEAR("--protocol-year", Type.INTEGER, MainJDEC.PROTOCOL_YEAR, getProtocolYears()),
    ROBOT_DRIVE_MODE("--robot-mode", Type.STRING, MainJDEC.ROBOT_DRIVE_MODE, getRobotModes()),
    TEAM_NUMBER("--team-num", Type.INTEGER, MainJDEC.TEAM_NUMBER),
    USB("--usb", Type.BOOLEAN, MainJDEC.USB_CONNECT),
    TEAM_PERSIST_FILE("--tpf", Type.STRING, null),
    DISABLE_HOTKEYS("--disable-hotkeys", Type.BOOLEAN, null);

    private final String flag;
    private final Type type;
    private final List<Object> options;
    private boolean present;
    private final JComponent jdecLink;
    private Object value;

    Parameter(String flag, Type type, JComponent jdecLink, Object... options) {
        this.flag = flag;
        this.type = type;
        this.jdecLink = jdecLink;
        this.options = Arrays.asList(options);
        this.present = false;
    }

    public String getFlag() {
        return flag;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        return String.valueOf(value);
    }

    public int getIntValue() {
        return Integer.parseInt(getStringValue());
    }

    public void setValue(Object value) {
        this.value = value;
    }

    private void setPresent(boolean present) {
        this.present = present;
    }

    public boolean isPresent() {
        return present;
    }

    private List<Object> getOptions() {
        return options;
    }

    private JComponent getJDECLink() {
        return jdecLink;
    }

    public static void parseArgs(String[] args) {
        List<String> argsList = Arrays.asList(args);
        for (Parameter p : Parameter.values()) {
            String flag = p.getFlag();
            if (argsList.contains(flag)) {
                p.setPresent(true);
                Type ptype = p.getType();
                if (ptype == Type.BOOLEAN) {
                    p.setValue(true);
                    continue;
                }
                try {
                    int ioFlag = argsList.indexOf(flag);
                    String value = argsList.get(ioFlag + 1);
                    if (p.getOptions().size() == 0 || p.getOptions().contains(value)) {
                        if (ptype == Type.INTEGER) {
                            p.setValue(Integer.parseInt(value));
                        } else {
                            p.setValue(value);
                        }
                    } else {
                        String fmt = "Illegal argument value %s for argument %s, valid options are: %s";
                        throw new IllegalArgumentException(String.format(fmt, p.getValue(), p.name(), p.getOptions()));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Argument value was not passed");
                }
            }
        }
    }

    public static void applyJDECLinks() {
        for (Parameter p : Parameter.values()) {
            JComponent jdecLink = p.getJDECLink();
            if (!p.isPresent() || jdecLink == null) {
                continue;
            }
            if (jdecLink instanceof JCheckBox) {
                ((JCheckBox) jdecLink).setSelected(true);
            } else if (jdecLink instanceof JComboBox) {
                JComboBox<?> jcb = ((JComboBox<?>) jdecLink);
                for (int i = 0; i < jcb.getItemCount(); i++) {
                    if (jcb.getItemAt(i).toString().equals(p.getStringValue())) {
                        jcb.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (jdecLink instanceof JTextField) {
                ((JTextField) jdecLink).setText(p.getStringValue());
            }
        }
    }

    private static Object[] getRobotModes() {
        RobotMode[] in = RobotMode.values();
        String[] out = new String[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i].toString();
        }
        return out;
    }

    private static Object[] getProtocolYears() {
        Integer[] in = DisplayEndpoint.VALID_PROTOCOL_YEARS;
        String[] out = new String[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = String.valueOf(in[i]);
        }
        return out;
    }

    public enum Type {
        INTEGER, STRING, BOOLEAN
    }
}
