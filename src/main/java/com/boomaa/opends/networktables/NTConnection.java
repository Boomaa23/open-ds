package com.boomaa.opends.networktables;

import com.boomaa.opends.networking.AddressConstants;
import com.boomaa.opends.networking.NetworkClock;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.EventSeverity;
import com.boomaa.opends.util.NumberUtils;

import java.io.IOException;
import java.util.List;

public class NTConnection extends Clock {
    private static final byte[] CLIENT_HELLO = getClientHello("opends");
    private static final byte[] CLIENT_HELLO_COMPLETE = new byte[] { 0x05 };
    private static final byte[] KEEP_ALIVE = new byte[] { 0x00 };
    public static byte[] CUTOFF_DATA = new byte[0];
    public static String SERVER_IDENTITY = "";
    public static boolean SERVER_SEEN_CLIENT = false;
    public static int SERVER_LATEST_VER = 0x0300;
    private TCPInterface connection;
    private boolean doReconnectSend = false;

    public NTConnection() {
        super(100);
    }

    @Override
    public void onCycle() {
        if (connection == null || connection.isClosed()) {
            Debug.println("NetworkTables connection failed",
                Debug.Options.create().setSeverity(EventSeverity.WARNING).setSticky(true));
            reloadConnection();
        } else {
            if (doReconnectSend) {
                Debug.println("NetworkTables connected to " + connection.toString());
                Debug.removeSticky("NetworkTables connection failed");
                connection.write(CLIENT_HELLO);
                decodeInput(connection.read());
                connection.write(CLIENT_HELLO_COMPLETE);
                decodeInput(connection.read());
                doReconnectSend = false;
            }
            connection.write(KEEP_ALIVE);
            decodeInput(connection.read());
        }
    }

    public void reloadConnection() {
        if (connection != null) {
            connection.close();
        }
        String rioIp = AddressConstants.getRioAddress();
        try {
            if (NetworkClock.exceptionPingTest(rioIp)) {
                connection = new TCPInterface(rioIp, AddressConstants.getRioPorts().getShuffleboard(), -1);
            }
        } catch (IOException ignored) {
        }
        doReconnectSend = true;
    }

    private void decodeInput(byte[] data) {
        if (data != null && data.length != 0) {
            int i = 0;
            while (i < data.length) {
                byte[] slicedData = ArrayUtils.slice(data, i);
                if (i == data.length - 1 && CUTOFF_DATA.length != 0) {
                    byte[] mergedData = new byte[slicedData.length + CUTOFF_DATA.length];
                    for (int j = 0; j < mergedData.length; j++) {
                        if (j < CUTOFF_DATA.length) {
                            mergedData[j] = CUTOFF_DATA[j];
                        } else {
                            mergedData[j] = slicedData[j - CUTOFF_DATA.length];
                        }
                    }
                    slicedData = mergedData;
                    CUTOFF_DATA = new byte[0];
                }
                int usedLen = new NTPacketData(slicedData).usedLength();
                if (usedLen == Integer.MAX_VALUE) {
                    break;
                }
                i += usedLen;
            }
        }
    }

    private static byte[] getClientHello(String clientName) {
        List<Byte> msg = NumberUtils.encodeULEB128(clientName);
        msg.add(0, (byte) 0x01);
        msg.add(1, (byte) 0x03);
        msg.add(2, (byte) 0x00);
        return ArrayUtils.byteListUnbox(msg);
    }
}
