package com.boomaa.opends.networking;

import java.io.OutputStream;
import java.net.Socket;

public class SimulatedRobot extends SimulateBase {
    public SimulatedRobot() {
        super(AddressConstants.getRioPorts(), new TCPServer(AddressConstants.getRioPorts().getTcp()) {
            @Override
            public void onRun(Socket client, OutputStream out, byte[] data) {

            }
        });
    }
}
