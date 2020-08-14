package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;

import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class StatsFrame extends PopupBase {
    public StatsFrame() {
        super("Statistics", new Dimension(520, 235));
    }

    @Override
    public void config() {
        super.config();
        content.setLayout(new GridBagLayout());
        Insets defInsets = new Insets(5, 5, 5, 5);
        GBCPanelBuilder labelBase = new GBCPanelBuilder(content).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END).setInsets(defInsets);
        GBCPanelBuilder itemsBase = labelBase.clone().setAnchor(GridBagConstraints.LINE_START);

        labelBase.clone().setPos(0, 0, 2, 1).setAnchor(GridBagConstraints.CENTER).build(new JLabel("RoboRIO"));
        labelBase.clone().setPos(0, 1, 1, 1).build(new JLabel("Disk Space: "));
        itemsBase.clone().setPos(1, 1, 1, 1).build(EmbeddedJDEC.DISK_SPACE);
        labelBase.clone().setPos(0, 2, 1, 1).build(new JLabel("RAM Space: "));
        itemsBase.clone().setPos(1, 2, 1, 1).build(EmbeddedJDEC.RAM_SPACE);
        labelBase.clone().setPos(0, 3, 1, 1).build(new JLabel("CPU Percent: "));
        itemsBase.clone().setPos(1, 3, 1, 1).build(EmbeddedJDEC.CPU_PERCENT);
        labelBase.clone().setPos(0, 4, 1, 1).build(new JLabel("RIO Version: "));
        itemsBase.clone().setPos(1, 4, 1, 1).build(EmbeddedJDEC.ROBORIO_VERSION);
        labelBase.clone().setPos(0, 5, 1, 1).build(new JLabel("WPILib Version: "));
        itemsBase.clone().setPos(1, 5, 1, 1).build(EmbeddedJDEC.WPILIB_VERSION);

        labelBase.clone().setPos(2, 0, 2, 1).setAnchor(GridBagConstraints.CENTER).build(new JLabel("CAN Bus"));
        labelBase.clone().setPos(2, 1, 1, 1).build(new JLabel("Utilization: "));
        itemsBase.clone().setPos(3, 1, 1, 1).build(EmbeddedJDEC.CAN_UTILIZATION);
        labelBase.clone().setPos(2, 2, 1, 1).build(new JLabel("Bus Off: "));
        itemsBase.clone().setPos(3, 2, 1, 1).build(EmbeddedJDEC.CAN_BUS_OFF);
        labelBase.clone().setPos(2, 3, 1, 1).build(new JLabel("TX Full: "));
        itemsBase.clone().setPos(3, 3, 1, 1).build(EmbeddedJDEC.CAN_TX_FULL);
        labelBase.clone().setPos(2, 4, 1, 1).build(new JLabel("RX Error: "));
        itemsBase.clone().setPos(3, 4, 1, 1).build(EmbeddedJDEC.CAN_RX_ERR);
        labelBase.clone().setPos(2, 5, 1, 1).build(new JLabel("TX Error: "));
        itemsBase.clone().setPos(3, 5, 1, 1).build(EmbeddedJDEC.CAN_TX_ERR);

        labelBase.clone().setPos(4, 0, 2, 1).setAnchor(GridBagConstraints.CENTER).build(new JLabel("Disable Faults"));
        labelBase.clone().setPos(4, 1, 1, 1).build(new JLabel("Comms: "));
        itemsBase.clone().setPos(5, 1, 1, 1).build(EmbeddedJDEC.DISABLE_FAULTS_COMMS);
        labelBase.clone().setPos(4, 2, 1, 1).build(new JLabel("12V: "));
        itemsBase.clone().setPos(5, 2, 1, 1).build(EmbeddedJDEC.DISABLE_FAULTS_12V);

        labelBase.clone().setPos(4, 3, 2, 1).setAnchor(GridBagConstraints.CENTER).build(new JLabel("Rail Faults"));
        labelBase.clone().setPos(4, 4, 1, 1).build(new JLabel("6V: "));
        itemsBase.clone().setPos(5, 4, 1, 1).build(EmbeddedJDEC.RAIL_FAULTS_6V);
        labelBase.clone().setPos(4, 5, 1, 1).build(new JLabel("5V: "));
        itemsBase.clone().setPos(5, 5, 1, 1).build(EmbeddedJDEC.RAIL_FAULTS_5V);
        labelBase.clone().setPos(4, 6, 1, 1).build(new JLabel("3.3V: "));
        itemsBase.clone().setPos(5, 6, 1, 1).build(EmbeddedJDEC.RAIL_FAULTS_3P3V);

        Dimension outWidth = new Dimension(75, 16);
        EmbeddedJDEC.DISK_SPACE.setPreferredSize(outWidth);
        EmbeddedJDEC.CAN_UTILIZATION.setPreferredSize(outWidth);
        EmbeddedJDEC.DISABLE_FAULTS_COMMS.setPreferredSize(outWidth);
        super.setResizable(false);
    }

    public interface EmbeddedJDEC {
        JLabel DISK_SPACE = new JLabel("");
        JLabel RAM_SPACE = new JLabel("");
        JLabel CPU_PERCENT = new JLabel("");
        JLabel ROBORIO_VERSION = new JLabel("");
        JLabel WPILIB_VERSION = new JLabel("");

        JLabel CAN_UTILIZATION = new JLabel("");
        JLabel CAN_BUS_OFF = new JLabel("");
        JLabel CAN_TX_FULL = new JLabel("");
        JLabel CAN_RX_ERR = new JLabel("");
        JLabel CAN_TX_ERR = new JLabel("");
        
        JLabel DISABLE_FAULTS_COMMS = new JLabel("");
        JLabel DISABLE_FAULTS_12V = new JLabel("");
        JLabel RAIL_FAULTS_6V = new JLabel("");
        JLabel RAIL_FAULTS_5V = new JLabel("");
        JLabel RAIL_FAULTS_3P3V = new JLabel("");
    }
}
