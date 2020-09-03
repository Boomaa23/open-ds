package com.boomaa.opends.networktables;

import com.boomaa.opends.networking.AddressConstants;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;

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
        this.connection = new TCPInterface(AddressConstants.getRioAddress(), AddressConstants.getRioPorts().getShuffleboard());
    }

    public void decode() {
    }

    public TCPInterface getConnection() {
        return connection;
    }
}
