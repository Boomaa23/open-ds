package com.boomaa.opends.networking;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.ParserNull;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.EventSeverity;
import com.boomaa.opends.util.PacketCounters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkClock extends Clock {
    private NetworkInterface iface;
    private final Remote remote;
    private final Protocol protocol;

    public NetworkClock(Remote remote, Protocol protocol) {
        super(createName(remote, protocol), remote == Remote.ROBO_RIO ? 20 : 500);
        this.remote = remote;
        this.protocol = protocol;
        reloadInterface();
    }

    @Override
    public void onCycle() {
        boolean connFms = remote != Remote.FMS || MainJDEC.FMS_CONNECT.isSelected();
        if (DisplayEndpoint.UPDATER != null && DisplayEndpoint.CREATOR != null) {
            if (connFms) {
                if (DisplayEndpoint.NET_IF_INIT.get(remote, protocol)) {
                    if (!iface.write(DisplayEndpoint.CREATOR.create(remote, protocol))) {
                        Debug.println(makeDebugStr("network error"), EventSeverity.WARNING, true);
                        DisplayEndpoint.UPDATER.update(ParserNull.getInstance(), remote, protocol);
                        reloadInterface();
                    } else {
                        byte[] data = iface.read();
                        if (data == null) {
                            Debug.println(makeDebugStr("invalid data"), EventSeverity.WARNING, true);
                            DisplayEndpoint.UPDATER.update(ParserNull.getInstance(), remote, protocol);
                            DisplayEndpoint.NET_IF_INIT.set(false, remote, protocol);
                        } else if (data.length != 0 || protocol != Protocol.UDP) {
                            PacketParser packetParser = DisplayEndpoint.getPacketParser(remote, protocol, data);
                            DisplayEndpoint.UPDATER.update(packetParser, remote, protocol);
                            Debug.println(remote + " " + protocol + " interface connected to " + iface.toString(), EventSeverity.INFO, true);
                            Debug.removeSticky(makeDebugStr("network error"));
                            Debug.removeSticky(makeDebugStr("invalid data"));
                        }
                    }
                } else {
                    Debug.println(makeDebugStr("network error"), EventSeverity.WARNING, true);
                    DisplayEndpoint.UPDATER.update(ParserNull.getInstance(), remote, protocol);
                    reloadInterface();
                }
                Debug.removeSticky(makeDebugStr("FMS not selected"));
            } else {
                DisplayEndpoint.UPDATER.update(ParserNull.getInstance(), remote, protocol);
                DisplayEndpoint.NET_IF_INIT.set(false, remote, protocol);
                Debug.println(makeDebugStr("FMS not selected"), EventSeverity.INFO, true);
            }
            Debug.removeSticky(makeDebugStr("updater or creator is null"));
        } else {
            Debug.println(makeDebugStr("updater or creator is null"), EventSeverity.ERROR, true);
        }
    }

    public void reloadInterface() {
        PacketCounters.get(remote, protocol).reset();
        if (DisplayEndpoint.UPDATER != null) {
            DisplayEndpoint.UPDATER.update(ParserNull.getInstance(), remote, protocol);
        }
        if (iface != null) {
            Debug.removeSticky(remote + " " + protocol + " interface connected to " + iface);
            iface.close();
            iface = null;
        }
        boolean isFms = remote == Remote.FMS;
        if (isFms && !MainJDEC.FMS_CONNECT.isSelected()) {
            DisplayEndpoint.NET_IF_INIT.set(false, remote, protocol);
            return;
        }
        try {
            String ip = isFms
                    ? AddressConstants.FMS_IP
                    : AddressConstants.getRioAddress();
            boolean reachable = exceptionPingTest(ip);
            if (!reachable) {
                uninitialize(isFms);
                return;
            }
            PortTriple ports = isFms
                    ? AddressConstants.getFMSPorts()
                    : AddressConstants.getRioPorts();
            iface = protocol == Protocol.TCP
                    ? new TCPInterface(ip, ports.getTcp())
                    : new UDPInterface(ip, ports.getUdpTx(), ports.getUdpRx());
            DisplayEndpoint.NET_IF_INIT.set(true, remote, protocol);
        } catch (IOException e) {
            uninitialize(isFms);
        }
    }

    public void restart() {
        super.end();
        DisplayEndpoint.NET_IF_INIT.set(false, remote, protocol);
        reloadInterface();
        super.start();
    }

    private void uninitialize(boolean isFms) {
        DisplayEndpoint.NET_IF_INIT.set(false, remote, protocol);
        if (!isFms) {
            MainJDEC.IS_ENABLED.setEnabled(false);
            if (MainJDEC.IS_ENABLED.isSelected()) {
                MainJDEC.IS_ENABLED.setSelected(false);
            }
        }
    }

    private static String createName(Remote remote, Protocol protocol) {
        String proto = protocol.name().charAt(0) + protocol.name().substring(1).toLowerCase();
        return (remote == Remote.ROBO_RIO ? "rio" : "fms") + proto;
    }

    private String makeDebugStr(String reason) {
        return remote + " " + protocol + " did not connect: " + reason;
    }

    public static boolean pingTest(String ip) {
        try {
            return exceptionPingTest(ip);
        } catch (IOException ignored) {
        }
        return false;
    }

    public static boolean exceptionPingTest(String ip) throws IOException {
        try {
            return InetAddress.getByName(ip).isReachable(1000);
        } catch (UnknownHostException ignored) {
            throw new IOException("Unknown host " + ip);
        }
    }
}
