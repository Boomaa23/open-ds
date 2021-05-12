package com.boomaa.opends.networktables;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.networking.AddressConstants;
import com.boomaa.opends.networking.NetworkReloader;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.NumberUtils;

import java.io.IOException;
import java.util.List;

public class NTConnection extends Clock {
    public static byte[] CUTOFF_DATA = new byte[0];
    public static String SERVER_IDENTITY = "";
    public static boolean SERVER_SEEN_CLIENT = false;
    public static int SERVER_LATEST_VER = 0x0300;
    private static final byte[] clientHello = getClientHello("opends");
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
                doReconnectSend = false;
            }
            decodeInput(connection.doInteract(new byte[] {0x00}));
        }
    }

    public void reloadConnection() {
        if (connection != null) {
            connection.close();
        }
        String rioIp = AddressConstants.getRioAddress(MainJDEC.USB_CONNECT.isSelected());
        try {
            NetworkReloader.exceptionPingTest(rioIp);
            connection = new TCPInterface(rioIp, AddressConstants.getRioPorts().getShuffleboard());
        } catch (IOException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e0) {
                e0.printStackTrace();
            }
        }
        doReconnectSend = true;
    }

    private void decodeInput(byte[] data) {
        if (data != null && data.length != 0) {
            int i = 0;
            while (i < data.length) {
                byte[] slicedData = ArrayUtils.sliceArr(data, i);
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
                i += new NTPacketData(slicedData).usedLength();
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
