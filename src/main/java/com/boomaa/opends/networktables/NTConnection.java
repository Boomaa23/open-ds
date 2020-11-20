package com.boomaa.opends.networktables;

import com.boomaa.opends.networking.AddressConstants;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NTConnection extends Clock {
    private static byte[] clientHello = { 0x01, 0x00, 0x03 };
    private TCPInterface connection;
    private boolean doReconnectSend = false;

    public NTConnection() {
        super(100);
    }

    @Override
    public void onCycle() {
        if (connection == null || connection.isClosed()) {
            reloadConnection();
        } else {
            if (doReconnectSend) {
                decodeInput(connection.doInteract(clientHello));
                decodeInput(connection.doInteract(new byte[] {0x00}));
                doReconnectSend = false;
            }
            try {
                decodeInput(connection.read());
            } catch (IOException ignored) {
            }
        }
    }

    public void reloadConnection() {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        try {
            String rioIp = AddressConstants.getRioAddress();
            InetAddress.getByName(rioIp);
            this.connection = new TCPInterface(rioIp, AddressConstants.getRioPorts().getShuffleboard());
        } catch (UnknownHostException | NumberFormatException ignored) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        doReconnectSend = true;
    }

    private void decodeInput(byte[] data) {
        if (data != null && data.length != 0) {
            int i = 0;
            while (i < data.length) {
                i += new NTPacketData(ArrayUtils.sliceArr(data, i)).usedLength();
            }
        }
    }
}
