package com.boomaa.opends.display;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.elements.HyperlinkBox;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.display.updater.ElementUpdater;
import com.boomaa.opends.headless.HeadlessController;
import com.boomaa.opends.networking.AddressConstants;
import com.boomaa.opends.networking.NetworkClock;
import com.boomaa.opends.networktables.NTConnection;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.DSLog;
import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.EventSeverity;
import com.boomaa.opends.util.InitChecker;
import com.boomaa.opends.util.Libraries;
import com.boomaa.opends.util.Parameter;
import com.github.kwhat.jnativehook.GlobalScreen;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DisplayEndpoint implements MainJDEC {
    public static final String CURRENT_VERSION_TAG = "v0.2.4";

    public static DSLog FILE_LOGGER = new DSLog();
    public static NTConnection NETWORK_TABLES = new NTConnection();
    public static InitChecker NET_IF_INIT = new InitChecker();
    public static Integer[] VALID_PROTOCOL_YEARS = { 2023, 2022, 2021, 2020, 2016, 2015, 2014 };

    private static final ProtocolClass parserClass = new ProtocolClass("com.boomaa.opends.data.receive.parser.Parser");
    private static final ProtocolClass creatorClass = new ProtocolClass("com.boomaa.opends.data.send.creator.Creator");
    private static final ProtocolClass updaterClass = new ProtocolClass("com.boomaa.opends.display.updater.Updater");
    private static final PPConstructorStore parserConstructors = new PPConstructorStore(parserClass);

    public static ElementUpdater UPDATER;
    public static PacketCreator CREATOR;

    public static final NetworkClock RIO_TCP_CLOCK = new NetworkClock(Remote.ROBO_RIO, Protocol.TCP);
    public static final NetworkClock RIO_UDP_CLOCK = new NetworkClock(Remote.ROBO_RIO, Protocol.UDP);
    public static final NetworkClock FMS_TCP_CLOCK = new NetworkClock(Remote.FMS, Protocol.TCP);
    public static final NetworkClock FMS_UDP_CLOCK = new NetworkClock(Remote.FMS, Protocol.UDP);

    private static final Clock controlUpdater = new Clock("controlUpdater", 2000) {
        @Override
        public void onCycle() {
            if (MainJDEC.FRAME.isShowing()) {
                System.gc();
                ControlDevices.updateValues();
                ControlDevices.checkForRemoval();
                ControlDevices.findAll();
            } else {
                super.end();
            }
        }
    };

    private DisplayEndpoint() {
    }

    public static void main(String[] args) {
        Debug.println("Starting OpenDS", EventSeverity.INFO, false, true);
        Parameter.parseArgs(args);
        Libraries.init();
        ControlDevices.init();
        LogManager.getLogManager().reset();
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        if (!Parameter.HEADLESS.isPresent()) {
            MainFrame.display();
        }
        doProtocolUpdate();
        Debug.println("Backend robot interface classes initialized.");

        RIO_TCP_CLOCK.start();
        RIO_UDP_CLOCK.start();
        FMS_TCP_CLOCK.start();
        FMS_UDP_CLOCK.start();
        if (!Parameter.DISABLE_LOG.isPresent()) {
            FILE_LOGGER.start();
        }
        if (!Parameter.DISABLE_NETTABLES.isPresent()) {
            NETWORK_TABLES.start();
        }
        checkForUpdates();

        controlUpdater.start();
        Debug.println("All threaded processes started");
        if (Parameter.HEADLESS.isPresent()) {
            HeadlessController.start();
        }
    }

    public static void doProtocolUpdate() {
        AddressConstants.reloadProtocol();
        parserClass.update();
        creatorClass.update();
        updaterClass.update();
        parserConstructors.update();
        try {
            UPDATER = (ElementUpdater) Class.forName(updaterClass.toString()).getConstructor().newInstance();
            CREATOR = (PacketCreator) Class.forName(creatorClass.toString()).getConstructor().newInstance();
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            MessageBox.show(ArrayUtils.printStackTrace(e, 10), MessageBox.Type.ERROR);
            System.exit(1);
        }
    }

    public static PacketParser getPacketParser(Remote remote, Protocol protocol, byte[] data) {
        try {
            return (PacketParser) parserConstructors.get(protocol, remote).newInstance(data);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
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
