package com.boomaa.opends.data;

import com.boomaa.opends.display.tabs.StatsTab;

import java.awt.Color;

public enum StatsFields {
    DISK_SPACE("Disk Space", DataSection.ROBO_RIO),
    RAM_SPACE("RAM Space", DataSection.ROBO_RIO),
    CPU_PERCENT("CPU Percent", DataSection.ROBO_RIO),
    ROBORIO_VERSION("RIO Version", DataSection.ROBO_RIO),
    WPILIB_VERSION("WPILib Version", DataSection.ROBO_RIO),
    CAN_UTILIZATION("Utilization %", DataSection.CAN_BUS),
    CAN_BUS_OFF("Bus Off", DataSection.CAN_BUS),
    CAN_TX_FULL("TX Full", DataSection.CAN_BUS),
    CAN_RX_ERR("RX Error", DataSection.CAN_BUS),
    CAN_TX_ERR("TX Error", DataSection.CAN_BUS),
    DISABLE_FAULTS_COMMS("Comms", DataSection.DISABLE_FAULTS),
    DISABLE_FAULTS_12V("12V", DataSection.DISABLE_FAULTS),
    RAIL_FAULTS_6V("6V", DataSection.RAIL_FAULTS),
    RAIL_FAULTS_5V("5V", DataSection.RAIL_FAULTS),
    RAIL_FAULTS_3P3V("3.3V", DataSection.RAIL_FAULTS);

    private final String key;
    private final DataSection section;
    private String value = "";

    StatsFields(String key, DataSection section) {
        this.key = key;
        this.section = section;
    }

    public void updateTableValue(Object value) {
        this.value = String.valueOf(value);
        if (this.ordinal() < StatsTab.TABLE_MODEL.getRowCount()) {
            StatsTab.TABLE_MODEL.setValueAt(value, this.ordinal(), 2);
        }
    }

    public String getKey() {
        return key;
    }

    public DataSection getSection() {
        return section;
    }

    public String getValue() {
        return value;
    }

    public enum DataSection {
        ROBO_RIO("RoboRIO", Color.BLUE),
        CAN_BUS("CAN Bus", Color.GREEN),
        DISABLE_FAULTS("Disable Faults", Color.ORANGE),
        RAIL_FAULTS("Rail Faults", Color.RED);

        private final String name;
        private final Color color;

        DataSection(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
