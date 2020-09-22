package com.boomaa.opends.networktables;

import com.boomaa.opends.networking.AddressConstants;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NTConnection extends Clock {
    private TCPInterface connection;

    public NTConnection() {
        super(20);
    }

    @Override
    public void onCycle() {
        if (connection != null && !connection.isClosed()) {
            byte[] data = connection.doInteract(new byte[0]);
            if (data != null && data.length != 0) {
                int i = 0;
                while (i < data.length) {
                    i += new NTPacketData(ArrayUtils.sliceArr(data, i)).usedLength() + 5; // 5x 0x00 of padding
                }
            }
        } else {
            reloadConnection();
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
    }

    public TCPInterface getConnection() {
        return connection;
    }
}
