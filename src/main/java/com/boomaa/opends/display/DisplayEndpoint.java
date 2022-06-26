package com.boomaa.opends.display;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.ParserNull;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.elements.HyperlinkBox;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.display.updater.ElementUpdater;
import com.boomaa.opends.networking.NetworkReloader;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.networking.UDPInterface;
import com.boomaa.opends.networktables.NTConnection;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.DSLog;
import com.boomaa.opends.util.InitChecker;
import com.boomaa.opends.util.Libraries;
import com.boomaa.opends.util.Parameter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisplayEndpoint implements MainJDEC {
    public static final String CURRENT_VERSION_TAG = "v0.2.3";

    public static DSLog FILE_LOGGER = new DSLog();
    public static NTConnection NETWORK_TABLES = new NTConnection();
    public static UDPInterface RIO_UDP_INTERFACE;
    public static TCPInterface RIO_TCP_INTERFACE;
    public static UDPInterface FMS_UDP_INTERFACE;
    public static TCPInterface FMS_TCP_INTERFACE;
    public static InitChecker NET_IF_INIT = new InitChecker();
    public static Integer[] VALID_PROTOCOL_YEARS = { 2022, 2021, 2020, 2016, 2015, 2014 };

    private static ElementUpdater updater;
    private static PacketCreator creator;
    private static final ProtocolClass parserClass = new ProtocolClass("com.boomaa.opends.data.receive.parser.Parser");
    private static final ProtocolClass creatorClass = new ProtocolClass("com.boomaa.opends.data.send.creator.Creator");
    private static final ProtocolClass updaterClass = new ProtocolClass("com.boomaa.opends.display.updater.Updater");

    private static final Clock rioTcpClock = new Clock("rioTcp", 20) {
        @Override
        public void onCycle() {
            if (updater != null && creator != null) {
                if (NET_IF_INIT.get(Remote.ROBO_RIO, Protocol.TCP)) {
                    byte[] rioTcpGet = RIO_TCP_INTERFACE.doInteract(creator.dsToRioTcp());
                    if (rioTcpGet == null) {
                        updater.updateFromRioTcp(ParserNull.getInstance());
                        NET_IF_INIT.set(false, Remote.ROBO_RIO, Protocol.TCP);
                    } else {
                        updater.updateFromRioTcp(getPacketParser("RioToDsTcp", rioTcpGet));
                    }
                } else {
                    updater.updateFromRioTcp(ParserNull.getInstance());
                    NetworkReloader.reloadRio(Protocol.TCP);
                }
            }
        }
    };

    private static final Clock rioUdpClock = new Clock("rioUdp", 20) {
        @Override
        public void onCycle() {
            if (updater != null && creator != null) {
                if (NET_IF_INIT.get(Remote.ROBO_RIO, Protocol.UDP)) {
                    RIO_UDP_INTERFACE.doSend(creator.dsToRioUdp());
                    byte[] rioUdpGet = RIO_UDP_INTERFACE.doReceieve();
                    if (rioUdpGet == null || rioUdpGet.length == 0) {
                        updater.updateFromRioUdp(ParserNull.getInstance());
                        NET_IF_INIT.set(false, Remote.ROBO_RIO, Protocol.UDP);
                    } else {
                        updater.updateFromRioUdp(getPacketParser("RioToDsUdp", rioUdpGet));
                    }
                } else {
                    updater.updateFromRioUdp(ParserNull.getInstance());
                    NetworkReloader.reloadRio(Protocol.UDP);
                }
            }
        }
    };

    private static final Clock fmsTcpClock = new Clock("fmsTcp", 500) {
        @Override
        public void onCycle() {
            if (updater != null && creator != null && MainJDEC.FMS_CONNECT.isSelected()) {
                if (NET_IF_INIT.get(Remote.FMS, Protocol.TCP)) {
                    byte[] fmsTcpGet = FMS_TCP_INTERFACE.doInteract(creator.dsToFmsTcp());
                    if (fmsTcpGet == null) {
                        updater.updateFromFmsTcp(ParserNull.getInstance());
                        NET_IF_INIT.set(false, Remote.FMS, Protocol.TCP);
                    } else {
                        updater.updateFromFmsTcp(getPacketParser("FmsToDsTcp", fmsTcpGet));
                    }
                } else {
                    updater.updateFromFmsTcp(ParserNull.getInstance());
                    NetworkReloader.reloadFms(Protocol.TCP);
                }
            } else if (!MainJDEC.FMS_CONNECT.isSelected() && FMS_TCP_INTERFACE != null) {
                updater.updateFromFmsTcp(ParserNull.getInstance());
                NET_IF_INIT.set(false, Remote.FMS, Protocol.TCP);
            }
        }
    };

    private static final Clock fmsUdpClock = new Clock("fmsUdp", 500) {
        @Override
        public void onCycle() {
            if (updater != null && creator != null && MainJDEC.FMS_CONNECT.isSelected()) {
                if (NET_IF_INIT.get(Remote.FMS, Protocol.UDP)) {
                    FMS_UDP_INTERFACE.doSend(creator.dsToFmsUdp());
                    byte[] fmsUdpGet = FMS_UDP_INTERFACE.doReceieve();
                    if (fmsUdpGet == null || fmsUdpGet.length == 0) {
                        updater.updateFromFmsUdp(ParserNull.getInstance());
                        NET_IF_INIT.set(false, Remote.FMS, Protocol.UDP);
                    } else {
                        updater.updateFromFmsUdp(getPacketParser("FmsToDsUdp", fmsUdpGet));
                    }
                } else {
                    updater.updateFromFmsUdp(ParserNull.getInstance());
                    NetworkReloader.reloadFms(Protocol.UDP);
                }
            } else if (!MainJDEC.FMS_CONNECT.isSelected() && FMS_UDP_INTERFACE != null) {
                updater.updateFromFmsUdp(ParserNull.getInstance());
                NET_IF_INIT.set(false, Remote.FMS, Protocol.UDP);
            }
        }
    };

    public static void main(String[] args) {
        Parameter.parseArgs(args);
        Libraries.init();
        ControlDevices.init();
        MainFrame.display();
        doProtocolUpdate();
        PROTOCOL_YEAR.addActionListener((e) -> doProtocolUpdate());
        rioTcpClock.start();
        rioUdpClock.start();
        fmsTcpClock.start();
        fmsUdpClock.start();
        NETWORK_TABLES.start();
        FILE_LOGGER.start();
        checkForUpdates();

        while (MainJDEC.FRAME.isShowing()) {
            System.gc();
            ControlDevices.updateValues();
            ControlDevices.checkForRemoval();
            ControlDevices.findAll();
            try {
                //TODO improve clock logic
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void doProtocolUpdate() {
        parserClass.update();
        creatorClass.update();
        updaterClass.update();
        try {
            updater = (ElementUpdater) Class.forName(updaterClass.toString()).getConstructor().newInstance();
            creator = (PacketCreator) Class.forName(creatorClass.toString()).getConstructor().newInstance();
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            MessageBox.show(ArrayUtils.printStackTrace(e, 10), MessageBox.Type.ERROR);
            System.exit(1);
        }
    }

    private static PacketParser getPacketParser(String name, byte[] data) {
        try {
            return (PacketParser) Class.forName(parserClass + "$" + name).getConstructor(byte[].class).newInstance(data);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void checkForUpdates() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://github.com/Boomaa23/open-ds/releases/latest").openConnection();
            connection.setConnectTimeout(1000);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                case HttpURLConnection.HTTP_SEE_OTHER:
                    String redirect = connection.getHeaderField("Location");
                    String remoteVer = redirect.substring(redirect.lastIndexOf("/") + 1);
                    if (!remoteVer.equals(CURRENT_VERSION_TAG)) {
                        new HyperlinkBox(String.format("A new version %s is available! Download from <br /><a href=\"%s\">%s</a>",
                                remoteVer, redirect, redirect)).display("New Version Available");
                    }
                    break;
            }
        } catch (IOException ignored) {
            System.err.println("WARNING: OpenDS update check failed. Ignore this warning if connected to a robot.");
        }
    }
}
