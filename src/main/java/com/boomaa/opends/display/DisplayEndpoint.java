package com.boomaa.opends.display;

import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.frames.FMSFrame;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.frames.RobotFrame;
import com.boomaa.opends.display.listeners.FMSTypeListener;
import com.boomaa.opends.display.listeners.SimRobotListener;
import com.boomaa.opends.display.updater.ElementUpdater;
import com.boomaa.opends.networking.SimulatedFMS;
import com.boomaa.opends.networking.SimulatedRobot;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.networking.UDPInterface;
import com.boomaa.opends.util.Clock;

import java.awt.event.ItemEvent;
import java.lang.reflect.InvocationTargetException;

public class DisplayEndpoint implements MainJDEC {
    public static UDPInterface RIO_UDP_INTERFACE;
    public static TCPInterface RIO_TCP_INTERFACE;
    public static UDPInterface FMS_UDP_INTERFACE;
    public static TCPInterface FMS_TCP_INTERFACE;
    public static SimulatedRobot SIM_ROBOT;
    public static SimulatedFMS SIM_FMS;
    public static RobotFrame ROBOT_FRAME;
    public static FMSFrame FMS_FRAME;
    private static ProtocolClass parserClass = new ProtocolClass("com.boomaa.opends.data.receive.parser.Parser");
    private static ProtocolClass creatorClass = new ProtocolClass("com.boomaa.opends.data.send.creator.Creator");
    private static ProtocolClass updaterClass = new ProtocolClass("com.boomaa.opends.display.updater.Updater");
    public static ItemEvent BLANK_ITEMEVENT = new ItemEvent(MainJDEC.FMS_TYPE, 0, null, ItemEvent.SELECTED);
    public static boolean HAS_INITIALIZED = false;

    private static final Clock twentyMsClock = new Clock(20) {
        @Override
        public void onCycle() {
            if (HAS_INITIALIZED) {
                try {
                    parserClass.update();
                    creatorClass.update();
                    updaterClass.update();
                    ElementUpdater updater = (ElementUpdater) Class.forName(updaterClass.toString()).getConstructor().newInstance();
                    PacketCreator creator = (PacketCreator) Class.forName(creatorClass.toString()).getConstructor().newInstance();
                    if (RIO_UDP_INTERFACE != null && !RIO_UDP_INTERFACE.isClosed()) {
                        updater.updateFromRioUdp(getPacketParser("RioToDsUdp", RIO_UDP_INTERFACE.doReceieve().getBuffer()));
                        RIO_UDP_INTERFACE.doSend(creator.dsToRioUdp());
                    }
                    if (RIO_TCP_INTERFACE != null && !RIO_TCP_INTERFACE.isClosed()) {
                        updater.updateFromRioTcp(getPacketParser("RioToDsTcp", RIO_TCP_INTERFACE.doInteract(creator.dsToRioTcp())));
                    }
                    if (FMS_UDP_INTERFACE != null && !FMS_UDP_INTERFACE.isClosed()) {
                        updater.updateFromFmsUdp(getPacketParser("FmsToDsUdp", FMS_UDP_INTERFACE.doReceieve().getBuffer()));
                        FMS_UDP_INTERFACE.doSend(creator.dsToFmsUdp());
                    }
                    if (FMS_TCP_INTERFACE != null && !FMS_TCP_INTERFACE.isClosed()) {
                        updater.updateFromFmsTcp(getPacketParser("FmsToDsTcp", FMS_TCP_INTERFACE.doInteract(creator.dsToFmsTcp())));
                    }
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                SimRobotListener.reload();
                FMSTypeListener.reload(DisplayEndpoint.BLANK_ITEMEVENT);
                HAS_INITIALIZED = true;
            }
        }
    };

    public static void main(String[] args) {
        MainFrame.display();
        twentyMsClock.start();
    }
    public static PacketParser getPacketParser(String name, byte[] data) {
        try {
            return (PacketParser) Class.forName(parserClass + "$" + name).getConstructor(byte[].class).newInstance(data);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer[] getValidProtocolYears() {
        return new Integer[] {
                2020
        };
    }
}
