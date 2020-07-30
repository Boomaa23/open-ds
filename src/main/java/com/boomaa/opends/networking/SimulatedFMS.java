package com.boomaa.opends.networking;

import java.io.OutputStream;
import java.net.Socket;

public class SimulatedFMS extends SimulateBase {
    public SimulatedFMS() {
        super(AddressConstants.FMS_PORTS, new TCPServer(AddressConstants.FMS_PORTS.getTcp()) {
            @Override
            public void onRun(Socket client, OutputStream out, byte[] data) {

            }
        });
        //TODO figure out if the "from UDP port 1145" matters for FMS
    }
}
