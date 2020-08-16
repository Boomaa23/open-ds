package com.boomaa.opends.networking;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.FMSType;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.ErrorBox;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkReloader extends DisplayEndpoint {
    public static void reloadRio(Protocol protocol) {
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
        } catch (UnknownHostException ignored) {
            unsetRio(protocol);
        } catch (IOException e) {
            ErrorBox.show(e.getMessage());
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

    public static void reloadFms(FMSType type, Protocol protocol) {
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

        switch (type) {
            case SIMULATED:
                //TODO robot simulation implementation
                break;
            case REAL:
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
                    MainJDEC.FMS_TYPE.setSelectedItem(FMSType.NONE);
                    ErrorBox.show(e.getMessage());
                    NET_IF_INIT.set(false, Remote.FMS, protocol);
                }
                break;
            case NONE:
                NET_IF_INIT.set(false, Remote.FMS, protocol);
                break;
        }
    }
}
