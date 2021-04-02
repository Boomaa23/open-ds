package com.boomaa.opends.networking;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.util.PacketCounters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkReloader extends DisplayEndpoint {
    public static void reloadRio(Protocol protocol) {
        PacketCounters.get(Remote.ROBO_RIO, protocol).reset();
        NETWORK_TABLES.reloadConnection();
        try {
            String rioIp = AddressConstants.getRioAddress();
            InetAddress.getByName(rioIp);
            PortTriple rioPorts = AddressConstants.getRioPorts();
            if (protocol == Protocol.UDP) {
                RIO_UDP_INTERFACE = new UDPInterface(rioIp, rioPorts.getUdpClient(), rioPorts.getUdpServer());
            }
            if (protocol == Protocol.TCP) {
                RIO_TCP_INTERFACE = new TCPInterface(rioIp, rioPorts.getTcp());
            }
            NET_IF_INIT.set(true, Remote.ROBO_RIO, protocol);
        } catch (NumberFormatException ignored) {
            unsetRio(protocol);
        } catch (IOException e) {
            e.printStackTrace();
            unsetRio(protocol);
        }
    }

    private static void unsetRio(Protocol protocol) {
        NET_IF_INIT.set(false, Remote.ROBO_RIO, protocol);
        MainJDEC.IS_ENABLED.setEnabled(false);
        if (MainJDEC.IS_ENABLED.isSelected()) {
            MainJDEC.IS_ENABLED.setSelected(false);
        }
    }

    public static void reloadFms(Protocol protocol) {
        PacketCounters.get(Remote.FMS, protocol).reset();
        if (protocol == Protocol.UDP) {
            if (FMS_UDP_INTERFACE != null) {
                FMS_UDP_INTERFACE.close();
            }
            FMS_UDP_INTERFACE = null;
        }
        if (protocol == Protocol.TCP) {
            if (FMS_TCP_INTERFACE != null) {
                FMS_TCP_INTERFACE.close();
            }
            FMS_TCP_INTERFACE = null;
        }

        if (FMS_CONNECT.isSelected()) {
            PortTriple fmsPorts = AddressConstants.getFMSPorts();
            String fmsIp = AddressConstants.getFMSIp();
            try {
                InetAddress.getByName(fmsIp);
                if (protocol == Protocol.UDP) {
                    FMS_UDP_INTERFACE = new UDPInterface(fmsIp, fmsPorts.getUdpClient(), fmsPorts.getUdpServer());
                }
                if (protocol == Protocol.TCP) {
                    FMS_TCP_INTERFACE = new TCPInterface(fmsIp, fmsPorts.getTcp());
                }
                NET_IF_INIT.set(true, Remote.FMS, protocol);
            } catch (IOException e) {
                e.printStackTrace();
                MainJDEC.FMS_CONNECT.setSelected(false);
                NET_IF_INIT.set(false, Remote.FMS, protocol);
            }
        } else {
            NET_IF_INIT.set(false, Remote.FMS, protocol);
        }
    }
}
